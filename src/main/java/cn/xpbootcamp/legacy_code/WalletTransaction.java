package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;

import javax.transaction.InvalidTransactionException;

public class WalletTransaction {
    private String transactionId;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createdTimestamp;
    private Double amount;
    private STATUS status;
    private String walletTransactionId;
    private RedisDistributedLock instance = RedisDistributedLock.getSingletonInstance();
    private WalletServiceImpl walletService = new WalletServiceImpl();


    public WalletTransaction(String transactionId, Long buyerId, Long sellerId, Long productId, String orderId) {
        if (transactionId != null && !transactionId.isEmpty()) {
            this.transactionId = transactionId;
        } else {
            this.transactionId = IdGenerator.generateTransactionId();
        }
        if (!this.transactionId.startsWith("t_")) {
            this.transactionId = "t_" + transactionId;
        }
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
    }

    public boolean execute() throws InvalidTransactionException {
        if (infoInvalid()) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        if (status == STATUS.EXECUTED) {
            return true;
        }
        boolean isLocked = instance.lock(transactionId);
        try {
            if (!isLocked) {
                return false;
            }

            if (dealOver20Days()) {
                setStatus(STATUS.EXPIRED);
                return false;
            }
            return moveMoney();
        } finally {
            if (isLocked) {
                instance.unlock(transactionId);
            }
        }
    }

    private boolean infoInvalid() {
        return buyerId == null || sellerId == null || amount < 0.0;
    }

    private boolean dealOver20Days() {
        return System.currentTimeMillis() - createdTimestamp > 1728000000;
    }

    private boolean moveMoney() {
        String walletTransactionId = walletService.moveMoney(transactionId, buyerId, sellerId, amount);
        if (walletTransactionId != null) {
            this.walletTransactionId = walletTransactionId;
            setStatus(STATUS.EXECUTED);
            return true;
        } else {
            setStatus(STATUS.FAILED);
            return false;
        }
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setInstance(RedisDistributedLock instance) {
        this.instance = instance;
    }

    public void setWalletService(WalletServiceImpl walletService) {
        this.walletService = walletService;
    }
}
