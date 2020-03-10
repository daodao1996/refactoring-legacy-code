package cn.xpbootcamp.legacy_code;

import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTransactionSpec {

  @Test
  void should_execute_throw_exception_when_buyerId_null() throws InvalidTransactionException {
    WalletTransaction walletTransaction = new WalletTransaction("t_11", null, 1L, 1L, "aa");
    assertThrows(InvalidTransactionException.class, walletTransaction::execute);
  }

  @Test
  void should_execute_throw_exception_when_sellerId_null() {
    WalletTransaction walletTransaction = new WalletTransaction("t_11", 1L, null, 1L, "aa");
    assertThrows(InvalidTransactionException.class, walletTransaction::execute);
  }
}
