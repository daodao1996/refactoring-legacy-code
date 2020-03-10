package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepositoryImpl;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalletServiceImplSpec {
  private UserRepositoryImpl userRepository = mock(UserRepositoryImpl.class);
  private WalletServiceImpl walletService = new WalletServiceImpl();

  @Test
  void should_return_null_when_amount_more_than_user_balance() {
    User user = new User(1L, 3);
    when(userRepository.find(1L)).thenReturn(user);
    walletService.setUserRepository(userRepository);

    assertNull(walletService.moveMoney("t_11", 1L, 2L, 10d));
  }

  @Test
  void should_return_transactionId_when_moveMoney_successful() {
    User seller = new User(1L, 3);
    User buyer = new User(2L, 10);
    when(userRepository.find(1L)).thenReturn(seller);
    when(userRepository.find(2L)).thenReturn(buyer);
    walletService.setUserRepository(userRepository);

    walletService.moveMoney("t_11", 2L, 1L, 5);

    assertEquals(8, seller.getBalance());
    assertEquals(5, buyer.getBalance());
  }
}
