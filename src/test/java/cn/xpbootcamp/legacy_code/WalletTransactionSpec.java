package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalletTransactionSpec {
  private WalletServiceImpl service = mock(WalletServiceImpl.class);
  private RedisDistributedLock distributedLock = mock(RedisDistributedLock.class);

  @Test
  void should_execute_throw_exception_when_buyerId_null(){
    WalletTransaction walletTransaction = new WalletTransaction("t_11", null, 1L, 1L, "aa");
    assertThrows(InvalidTransactionException.class, walletTransaction::execute);
  }

  @Test
  void should_execute_throw_exception_when_sellerId_null() {
    WalletTransaction walletTransaction = new WalletTransaction("t_11", 1L, null, 1L, "aa");
    assertThrows(InvalidTransactionException.class, walletTransaction::execute);
  }

  @Test
  void should_execute_return_true_when_status_is_executed() throws InvalidTransactionException {
    WalletTransaction walletTransaction = new WalletTransaction("t_11", 1L, 1L, 1L, "aa");
    walletTransaction.setStatus(STATUS.EXECUTED);
    walletTransaction.setAmount(1D);
    assertTrue(walletTransaction.execute());
  }

  @Test
  void should_execute_throw_exception_when_amount_less_than_0(){
    WalletTransaction walletTransaction = new WalletTransaction("t_11", 1L, 1L, 1L, "aa");
    walletTransaction.setAmount(-1D);
    assertThrows(InvalidTransactionException.class, walletTransaction::execute);
  }

  @Test
  void should_execute_return_false_when_moveMoney_return_null() throws InvalidTransactionException {
    when(service.moveMoney("t_11", 1L, 2L, 10D)).thenReturn(null);
    when(distributedLock.lock("t_11")).thenReturn(true);
    WalletTransaction walletTransaction = new WalletTransaction("t_11", 1L, 2L, 1L, "aa");
    walletTransaction.setAmount(10D);
    walletTransaction.setInstance(distributedLock);
    walletTransaction.setWalletService(service);
    assertFalse(walletTransaction.execute());
    assertEquals(STATUS.FAILED, walletTransaction.getStatus());
  }

}
