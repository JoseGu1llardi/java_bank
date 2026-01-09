import application.repositories.AccountRepository;
import application.repositories.TransactionRepository;
import application.repositories.UserRepository;
import application.services.AccountService;
import application.services.Transactionservice;
import application.services.UserService;
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

        try {
            System.out.println("========== BANKING SYSTEM ==========");

            System.out.println("CREATING USERS...");
            User jose = new User("Jose Guillard", "438-900-898-60", "junior11_junior@hotmail.com");
            User leticia = new User("Leticia Castro Martins Silva", "000-000-000-10", "leticiacms11@hotmail.com");

            userRepository.save(jose);
            userRepository.save(leticia);

            System.out.println("Users created successfully: " + jose.getName() + ", " + leticia.getName());
            System.out.println();

            System.out.println("CREATING ACCOUNTS...");
            CheckingAccount joseCheckingAccount = new CheckingAccount("0001", jose);
            SavingsAccount joseSavingsAccount = new SavingsAccount("0001", jose);
            CheckingAccount leticiaCheckingAccount = new CheckingAccount("0001", leticia);

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

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
