import application.repositories.AccountRepository;
import application.repositories.TransactionRepository;
import application.repositories.UserRepository;
import application.services.AccountService;
import application.services.Transactionservice;
import application.services.UserService;
import domain.enums.TransactionType;
import domain.exception.EmailAlreadyInUseException;
import domain.exception.InsufficientFundsException;
import domain.model.Account;
import domain.model.CheckingAccount;
import domain.model.SavingsAccount;
import domain.model.Transaction;
import domain.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JavaBank {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        UserService userService = new UserService(userRepository);
        AccountService accountService = new AccountService(accountRepository, userRepository);
        Transactionservice transactionService = new Transactionservice(accountRepository, transactionRepository);

        try {

            // ================================================================
            section("1. USER REGISTRATION");
            // ================================================================

            User jose = userService.createUser(
                    "Jose Guillard", "438-900-898-60", "junior11_junior@hotmail.com");
            User leticia = userService.createUser(
                    "Leticia Castro", "000-000-000-10", "leticiacms11@hotmail.com");
            System.out.println("Registered: " + jose.getName() + ", " + leticia.getName());

            // Invalid CPF: all same digits
            try {
                userService.createUser("Invalid", "111-111-111-11", "valid@email.com");
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] All-same-digit CPF rejected: " + e.getMessage());
            }

            // Invalid CPF: too few digits
            try {
                userService.createUser("Invalid", "123-456", "valid2@email.com");
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Short CPF rejected: " + e.getMessage());
            }

            // Invalid email format
            try {
                userService.createUser("Invalid", "999-888-777-66", "not-an-email");
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Malformed email rejected: " + e.getMessage());
            }


            // ================================================================
            section("2. ACCOUNT CREATION");
            // ================================================================

            // Polymorphism: concrete types held in the abstract Account variable
            Account joseChecking    = accountService.createCheckingAccount("0001", jose.getId());
            Account joseSavings     = accountService.createSavingAccount("0001", jose.getId());
            Account leticiaChecking = accountService.createCheckingAccount("0001", leticia.getId());

            System.out.println("Jose    | Checking : " + joseChecking.getAccountCode());
            System.out.println("Jose    | Savings  : " + joseSavings.getAccountCode());
            System.out.println("Leticia | Checking : " + leticiaChecking.getAccountCode());

            List<Account> joseAccounts = accountService.searchForUserAccounts(jose.getId());
            System.out.println("Jose's total accounts: " + joseAccounts.size());


            // ================================================================
            section("3. DEPOSITS");
            // ================================================================

            transactionService.deposit(joseChecking.getAccountCode(),    BigDecimal.valueOf(1500));
            transactionService.deposit(joseSavings.getAccountCode(),     BigDecimal.valueOf(1000));
            transactionService.deposit(leticiaChecking.getAccountCode(), BigDecimal.valueOf(2000));

            System.out.printf("Jose Checking   : $%.2f%n", joseChecking.getBalance());
            System.out.printf("Jose Savings    : $%.2f%n", joseSavings.getBalance());
            System.out.printf("Leticia Checking: $%.2f%n", leticiaChecking.getBalance());

            // Negative amount must be rejected
            try {
                transactionService.deposit(joseChecking.getAccountCode(), BigDecimal.valueOf(-50));
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Negative deposit rejected: " + e.getMessage());
            }


            // ================================================================
            section("4. WITHDRAWALS — POLYMORPHISM");
            // ================================================================

            // Checking account: overdraft allows balance to go negative
            System.out.printf("Jose Checking before: $%.2f  (overdraft limit: $%.2f)%n",
                    joseChecking.getBalance(),
                    ((CheckingAccount) joseChecking).getOverdraftLimit());

            transactionService.withdraw(joseChecking.getAccountCode(), BigDecimal.valueOf(1800));
            System.out.printf("Withdrew $1800 (used $300 of overdraft). New balance: $%.2f%n",
                    joseChecking.getBalance());

            // Attempt to exceed total available (balance + overdraft)
            try {
                transactionService.withdraw(joseChecking.getAccountCode(), BigDecimal.valueOf(500));
            } catch (IllegalStateException e) {
                System.out.println("[EXPECTED] Over-overdraft rejected: " + e.getMessage());
            }

            // Savings account: strict — no overdraft allowed
            System.out.printf("%nJose Savings before: $%.2f%n", joseSavings.getBalance());

            transactionService.withdraw(joseSavings.getAccountCode(), BigDecimal.valueOf(200));
            System.out.printf("Withdrew $200. New balance: $%.2f%n", joseSavings.getBalance());

            try {
                transactionService.withdraw(joseSavings.getAccountCode(), BigDecimal.valueOf(1000));
            } catch (InsufficientFundsException e) {
                System.out.println("[EXPECTED] Savings overdraft rejected: " + e.getMessage());
            }


            // ================================================================
            section("5. TRANSFERS");
            // ================================================================

            System.out.printf("Before — Leticia Checking: $%.2f | Jose Savings: $%.2f%n",
                    leticiaChecking.getBalance(), joseSavings.getBalance());

            transactionService.transfer(
                    leticiaChecking.getAccountCode(),
                    joseSavings.getAccountCode(),
                    BigDecimal.valueOf(500)
            );

            System.out.printf("After  — Leticia Checking: $%.2f | Jose Savings: $%.2f%n",
                    leticiaChecking.getBalance(), joseSavings.getBalance());

            // Transfer exceeding available balance
            try {
                transactionService.transfer(
                        leticiaChecking.getAccountCode(),
                        joseChecking.getAccountCode(),
                        BigDecimal.valueOf(9999)
                );
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Transfer over balance rejected: " + e.getMessage());
            }


            // ================================================================
            section("6. MONTHLY FEE — CHECKING ACCOUNTS ONLY");
            // ================================================================

            // Jose's balance is -$300: below $1,000 threshold — fee is charged
            System.out.printf("Jose Checking before fee : $%.2f%n", joseChecking.getBalance());
            transactionService.chargeFee(joseChecking.getAccountCode());
            System.out.printf("Jose Checking after fee  : $%.2f  (fee $12.00 deducted)%n",
                    joseChecking.getBalance());

            // Leticia's balance is $1,500: above $1,000 threshold — fee is waived
            System.out.printf("%nLeticia Checking before fee: $%.2f%n", leticiaChecking.getBalance());
            transactionService.chargeFee(leticiaChecking.getAccountCode());
            System.out.printf("Leticia Checking after fee : $%.2f  (fee waived — balance >= $1000)%n",
                    leticiaChecking.getBalance());

            // Fee on savings account must be rejected
            try {
                transactionService.chargeFee(joseSavings.getAccountCode());
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Fee on savings rejected: " + e.getMessage());
            }


            // ================================================================
            section("7. YIELD — SAVINGS ACCOUNTS ONLY");
            // ================================================================

            // Yield on a checking account must be rejected
            try {
                transactionService.applyYield(joseChecking.getAccountCode());
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Yield on checking rejected: " + e.getMessage());
            }

            // Yield on savings: applied once per anniversary date (0.5% rate)
            // Note: if the account was created today, the yield is not applied twice
            // in the same period — the next yield will be available next month.
            System.out.printf("%nJose Savings balance     : $%.2f%n", joseSavings.getBalance());
            System.out.printf("Estimated monthly yield  : $%.2f (0.5%%)%n",
                    ((SavingsAccount) joseSavings).getEstimatedYield());
            transactionService.applyYield(joseSavings.getAccountCode());
            System.out.printf("Jose Savings after yield : $%.2f%n", joseSavings.getBalance());


            // ================================================================
            section("8. STATEMENTS & REPORTING");
            // ================================================================

            System.out.println("-- Jose Savings: full statement --");
            List<Transaction> savingsStatement =
                    transactionService.getStatement(joseSavings.getAccountCode());
            savingsStatement.forEach(System.out::println);

            System.out.println("\n-- Jose Checking: last 2 transactions --");
            transactionService.getRecentStatement(joseChecking.getAccountCode(), 2)
                    .forEach(System.out::println);

            System.out.println("\n-- Leticia Checking: totals by transaction type --");
            Map<TransactionType, BigDecimal> expenses =
                    transactionService.getStatementExpensesByType(leticiaChecking.getAccountCode());
            expenses.forEach((type, total) ->
                    System.out.printf("  %-20s $%.2f%n", type.getDescription(), total));


            // ================================================================
            section("9. USER EMAIL MANAGEMENT");
            // ================================================================

            System.out.println("Jose's current email : " + jose.getEmail());
            userService.updateEmail(jose.getId(), "jose.updated@email.com");
            System.out.println("Jose's updated email : " + jose.getEmail());

            // Same email — already in use by the user themselves
            try {
                userService.updateEmail(jose.getId(), "jose.updated@email.com");
            } catch (EmailAlreadyInUseException e) {
                System.out.println("[EXPECTED] Same email rejected: " + e.getMessage());
            }

            // Email already registered to another user
            try {
                userService.updateEmail(jose.getId(), "leticiacms11@hotmail.com");
            } catch (EmailAlreadyInUseException e) {
                System.out.println("[EXPECTED] Email in use by another user: " + e.getMessage());
            }


            // ================================================================
            section("10. ACCOUNT DEACTIVATION");
            // ================================================================

            // Cannot deactivate an account that still has a balance
            try {
                accountService.deactivateAccount(leticiaChecking.getAccountCode());
            } catch (IllegalArgumentException e) {
                System.out.println("[EXPECTED] Deactivation with balance rejected: " + e.getMessage());
            }

            // Zero out the balance, then deactivate
            transactionService.withdraw(leticiaChecking.getAccountCode(), leticiaChecking.getBalance());
            System.out.printf("Leticia Checking zeroed: $%.2f%n", leticiaChecking.getBalance());

            accountService.deactivateAccount(leticiaChecking.getAccountCode());
            System.out.println("Leticia Checking deactivated. Active: " + leticiaChecking.isActive());

            // Any operation on an inactive account must be rejected
            try {
                transactionService.deposit(leticiaChecking.getAccountCode(), BigDecimal.valueOf(100));
            } catch (IllegalStateException e) {
                System.out.println("[EXPECTED] Operation on inactive account rejected: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}