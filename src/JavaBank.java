import application.repositories.AccountRepository;
import application.repositories.TransactionRepository;
import application.repositories.UserRepository;
import application.services.AccountService;
import application.services.Transactionservice;
import application.services.UserService;
import domain.exception.InsufficientFundsException;
import domain.model.CheckingAccount;
import domain.model.SavingsAccount;
import domain.model.User;

import java.math.BigDecimal;

public class JavaBank {

    public static void main(String[] args) {
        // Initialization of the components
        UserRepository userRepository = new UserRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        UserService userService = new UserService(userRepository);
        AccountService accountService = new AccountService(accountRepository, userRepository);
        Transactionservice transactionservice = new Transactionservice(accountRepository, transactionRepository);

        // Executes banking system simulation; handles exceptions
        try {
            System.out.println("========== BANKING SYSTEM ==========");

            System.out.println("CREATING USERS...");

            User jose = new User(
                    "Jose Guillard",
                    "438-900-898-60",
                    "junior11_junior@hotmail.com"
            );
            User elisa = new User(
                    "Elisa Pontes",
                    "000-000-000-10",
                    "elisa.pontes@hotmail.com"
            );

            userRepository.save(jose);
            userRepository.save(elisa);

            System.out.println("Users created successfully: " + jose.getName() + ", " + elisa.getName());
            System.out.println();


            // Create Accounts - Demonstrate POLYMORPHISM
            System.out.println("CREATING ACCOUNTS...");

            // Both variables are of the abstract type Account
            CheckingAccount joseCheckingAccount = new CheckingAccount("0001", jose);
            SavingsAccount joseSavingsAccount = new SavingsAccount("0001", jose);
            CheckingAccount leticiaCheckingAccount = new CheckingAccount("0001", elisa);

            accountRepository.save(joseCheckingAccount);
            accountRepository.save(joseSavingsAccount);
            accountRepository.save(leticiaCheckingAccount);

            System.out.println("Checking Account Jose: " + joseCheckingAccount.getAccountCode());
            System.out.println("Saving Account Jose: " + joseSavingsAccount.getAccountCode());
            System.out.println("Checking Account Leticia: " + leticiaCheckingAccount.getAccountCode());
            System.out.println();

            System.out.println("MAKING DEPOSIT...");

            transactionservice.deposit(joseCheckingAccount.getAccountCode(), BigDecimal.valueOf(1500));
            transactionservice.deposit(joseSavingsAccount.getAccountCode(), BigDecimal.valueOf(1000));
            transactionservice.deposit(leticiaCheckingAccount.getAccountCode(), BigDecimal.valueOf(2000));

            System.out.println(transactionservice.getStatement(joseCheckingAccount.getAccountCode()));
            System.out.println(transactionservice.getStatement(joseSavingsAccount.getAccountCode()));
            System.out.println(transactionservice.getStatement(leticiaCheckingAccount.getAccountCode()));
            System.out.println();

            // Demonstrate POLYMORPHISM - different behaviors
            System.out.println("TESTING WITHDRAWALS...");

            // Withdraw from Checking Account - Can use overdraft
            System.out.println("Trying withdraw $1.800 from the checking account (balance: $1.500)");
            transactionservice.withdraw(joseCheckingAccount.getAccountCode(), BigDecimal.valueOf(1800));
            System.out.println("Withdraw approved! New balance: " + joseCheckingAccount.getBalance());
            System.out.println("(Using $300 from the overdraft)");
            System.out.println();

            // Attempt to withdraw from a savings account - does not allow negative balance
            System.out.println("Trying withdraw $300 from the savings account (balance: $1000)");
            try {
                transactionservice.withdraw(joseSavingsAccount.getAccountCode(), BigDecimal.valueOf(1200));
            } catch (InsufficientFundsException e) {
                System.out.println("Withdraw denied: " + e.getMessage());
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
