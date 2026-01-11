# 🏦 Banking System - Demonstration of the 4 OOP Pillars

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/Status-Educational-blue?style=flat-square)

> Educational banking system developed in pure Java to demonstrate the practical application of the 4 pillars of Object-Oriented Programming: **Encapsulation**, **Abstraction**, **Inheritance**, and **Polymorphism**.

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [OOP Pillars Applied](#oop-pillars-applied)
- [Architecture](#architecture)
- [Folder Structure](#folder-structure)
- [How to Run](#how-to-run)
- [Usage Examples](#usage-examples)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 About the Project

This project was developed as an educational tool to learn and practice the fundamental concepts of Object-Oriented Programming. Through a familiar domain (banking system), it demonstrates how to apply each of the 4 OOP pillars in a practical and scalable way.

### Learning Objectives

- ✅ Understand and apply **Encapsulation** to protect data
- ✅ Use **Abstraction** to define contracts and common behaviors
- ✅ Implement **Inheritance** to reuse code and create hierarchies
- ✅ Explore **Polymorphism** to create flexible and extensible code

---

## ⚡ Features

### User Management
- ✨ Bank customer registration
- ✨ Social Security Number validation
- ✨ Personal data management

### Account Types
- 💳 **Checking Account**
    - Overdraft protection ($500.00)
    - Monthly fee ($12.00)
    - Allows negative balance up to limit

- 💰 **Savings Account**
    - Monthly yield (0.5%)
    - No fees
    - Does not allow negative balance

### Banking Operations
- 💵 Deposit
- 💸 Withdrawal (with specific rules per account type)
- 🔄 Transfer between accounts
- 📊 Balance inquiry
- 📝 Detailed transaction statement

### Administrative Features
- 📈 Apply yields (savings accounts)
- 💰 Charge fees (checking accounts)
- 🔍 List accounts by user

---

## 🎓 OOP Pillars Applied

### 1️⃣ Encapsulation

**Where**: `User`, `Account`, `Transaction` classes

```java
// Private attributes with validation
private String ssn;

private String validateSsn(String ssn) {
    String cleanSsn = ssn.replaceAll("[^0-9]", "");
    if (cleanSsn.length() != 11) {
        throw new IllegalArgumentException("Invalid SSN");
    }
    return cleanSsn;
}

// Controlled access via getter
public String getSsn() { return ssn; }
```

**Benefits**:
- Protection against invalid modifications
- Centralized validation
- Immutable transaction history

---

### 2️⃣ Abstraction

**Where**: Abstract class `Account`

```java
public abstract class Account {
    // Common behavior implemented
    public void deposit(double amount) {
        validateAmount(amount);
        this.balance += amount;
        recordTransaction("DEPOSIT", amount, null);
    }
    
    // Specific behavior delegated to subclasses
    public abstract void withdraw(double amount);
    public abstract double calculateMonthlyFee();
}
```

**Benefits**:
- Defines "contract" for all accounts
- Reuses common code
- Enforces consistency between implementations

---

### 3️⃣ Inheritance

**Where**: `CheckingAccount` and `SavingsAccount` inherit from `Account`

```java
public class CheckingAccount extends Account {
    private double overdraftLimit;
    
    public CheckingAccount(String branch, User owner) {
        super(branch, owner);  // Reuses parent constructor
        this.overdraftLimit = 500.0;
    }
    
    // Inherits: deposit(), transfer(), getters...
}
```

**Benefits**:
- Code reuse
- Logical and natural hierarchy
- Easy to add new account types

---

### 4️⃣ Polymorphism

**Where**: Overridden methods and polymorphic collections

```java
// Same method, different behaviors
Account cc = new CheckingAccount(...);
Account sa = new SavingsAccount(...);

cc.withdraw(1500);  // Allows overdraft
sa.withdraw(1500);  // Throws exception if insufficient balance

// Polymorphic collections
List<Account> accounts = new ArrayList<>();
accounts.add(new CheckingAccount(...));
accounts.add(new SavingsAccount(...));

accounts.forEach(account -> {
    // Calls the correct method at runtime
    double fee = account.calculateMonthlyFee();
});
```

**Benefits**:
- Flexible and extensible code
- Reduces duplication
- Facilitates maintenance

---

## 🏗️ Architecture

The project follows a layered architecture, promoting separation of concerns:

```
┌─────────────────────────────────────┐
│   Application (Main)                │  ← Entry point
├─────────────────────────────────────┤
│   Service Layer                     │  ← Business logic
│   • UserService                     │
│   • AccountService                  │
│   • TransactionService              │
├─────────────────────────────────────┤
│   Model Layer                       │  ← Domain entities
│   • User                            │
│   • Account (abstract)              │
│   • CheckingAccount, SavingsAccount │
│   • Transaction                     │
├─────────────────────────────────────┤
│   Repository Layer                  │  ← Persistence
│   • UserRepository                  │
│   • AccountRepository               │
│   • TransactionRepository           │
└─────────────────────────────────────┘
```

### Design Patterns Implemented

- 🎨 **Template Method**: `Account` class with `transfer()` method
- 🎨 **Repository Pattern**: Decoupled persistence layer
- 🎨 **Service Layer**: Centralized business logic
- 🎨 **Dependency Injection**: Injection via constructor

---

## 📁 Folder Structure

```
banking-system/
│
├── src/
│   └── com/
│       └── bank/
│           ├── model/
│           │   ├── User.java
│           │   ├── Account.java
│           │   ├── CheckingAccount.java
│           │   ├── SavingsAccount.java
│           │   └── Transaction.java
│           │
│           ├── service/
│           │   ├── UserService.java
│           │   ├── AccountService.java
│           │   └── TransactionService.java
│           │
│           ├── repository/
│           │   ├── UserRepository.java
│           │   ├── AccountRepository.java
│           │   └── TransactionRepository.java
│           │
│           ├── exception/
│           │   ├── InsufficientBalanceException.java
│           │   ├── AccountNotFoundException.java
│           │   └── UserNotFoundException.java
│           │
│           └── BankingApplication.java
│
├── docs/
│   └── oop-guide.md
│
└── README.md
```

---

## 🚀 How to Run

### Prerequisites

- Java JDK 17 or higher
- IDE of your choice (IntelliJ IDEA, Eclipse, VS Code)

### Steps

1. **Clone the repository** (or copy the files)

```bash
git clone https://github.com/JoseGu1llardi/java_bank#
cd java_bank
```

2. **Compile the project**

```bash
# Via command line
javac -d bin src/com/bank/**/*.java

# Or use your favorite IDE
```

3. **Run the application**

```bash
java -cp bin com.bank.BankingApplication
```

### Expected Output

```
=== BANKING SYSTEM - DEMONSTRATION ===

1. CREATING USERS
✓ Users created: John Silva, Mary Santos

2. CREATING ACCOUNTS
✓ Checking Account John: 12345678
✓ Savings Account John: 87654321
✓ Checking Account Mary: 11223344

3. MAKING DEPOSITS
✓ Checking Account Balance John: $ 1000.0
✓ Savings Account Balance John: $ 5000.0
...
```

---

## 💡 Usage Examples

### Create a User

```java
UserService userService = new UserService(new UserRepository());

User john = userService.createUser(
    "John Silva", 
    "123-456-789-00", 
    "john@email.com"
);
```

### Create Accounts

```java
AccountService accountService = new AccountService(
    new AccountRepository(), 
    new UserRepository()
);

// Polymorphism: both return Account type
Account checkingAccount = accountService.createCheckingAccount("0001", john.getId());
Account savingsAccount = accountService.createSavingsAccount("0001", john.getId());
```

### Perform Operations

```java
TransactionService transactionService = new TransactionService(
    new AccountRepository(),
    new TransactionRepository()
);

// Deposit
transactionService.deposit("0001", checkingAccount.getNumber(), 1000.0);

// Withdrawal (polymorphism: each type has its own rule)
transactionService.withdraw("0001", checkingAccount.getNumber(), 1200.0); // ✓ OK with overdraft
transactionService.withdraw("0001", savingsAccount.getNumber(), 6000.0);  // ✗ Insufficient balance

// Transfer
transactionService.transfer(
    "0001", checkingAccount.getNumber(),
    "0001", savingsAccount.getNumber(),
    500.0
);

// Query statement
List<Transaction> statement = transactionService.getStatement("0001", checkingAccount.getNumber());
statement.forEach(System.out::println);
```

### Administrative Operations

```java
// Apply yields only to savings accounts
transactionService.applyMonthlyInterest();

// Charge fees only from checking accounts
transactionService.chargeMonthlyFees();
```

---

## 🔮 Future Enhancements

### Planned Features

#### 🔐 Authentication System
```java
public class Credential {
    private String passwordHash;
    private LocalDateTime lastAccess;
    private int failedAttempts;
}

public class AuthenticationService {
    public boolean authenticate(String ssn, String password);
    public void changePassword(String userId, String oldPassword, String newPassword);
}
```

#### 💎 Premium Account
```java
public class PremiumCheckingAccount extends CheckingAccount {
    @Override
    public double calculateMonthlyFee() {
        return this.balance > 5000 ? 0.0 : 12.0;
    }
}
```

#### 📊 Limits and Controls
```java
public abstract class Account {
    protected double dailyWithdrawalLimit;
    protected double dailyTransferLimit;
    protected Map<LocalDate, Double> dailyOperations;
}
```

#### 📈 Advanced Reports
```java
public class ReportService {
    public List<Transaction> findByPeriod(LocalDate start, LocalDate end);
    public Map<String, Double> groupByType(List<Transaction> transactions);
    public double calculateMonthlyAverage(Account account);
}
```

#### 🗄️ Real Persistence
```java
// Replace HashMap with JDBC or JPA
public class AccountRepositoryJDBC implements IAccountRepository {
    @Override
    public void save(Account account) {
        // Real database connection
    }
}
```

#### 🔔 Notification System
```java
public interface AccountObserver {
    void onTransaction(Transaction transaction);
    void onLowBalance(double balance);
}
```

---

## 🤝 Contributing

Contributions are welcome! This is an educational project and improvements are always appreciated.

### How to Contribute

1. Fork the project
2. Create a feature branch (`git checkout -b feature/NewFeature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

### Contribution Suggestions

- 📝 Improve documentation
- 🧪 Add unit tests
- ✨ Implement new features
- 🐛 Fix bugs
- 🎨 Improve design patterns

---

## 📚 Additional Resources

### Project Documentation

- 📖 [Complete Guide to the 4 OOP Pillars](docs/oop-guide.md)
- 🎯 [UML Diagrams](docs/diagrams/)
- 📊 [Use Cases](docs/use-cases.md)

### Learning

- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Effective Java - Joshua Bloch](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Head First Design Patterns](https://www.oreilly.com/library/view/head-first-design/0596007124/)

---

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

## 👨‍💻 Author

Developed as an educational project to demonstrate OOP concepts.

---

## ⭐ Acknowledgments

- Thank you for using this project as study material!
- If this project helped you, consider giving it a ⭐
- Share it with other programming students!

---

## 📞 Contact

For questions, suggestions, or feedback:

- 📧 Email: junior11_junior@hotmail.com
- 💼 LinkedIn: [Jose Guillard](https://www.linkedin.com/in/jos%C3%A9-wellington-ribeiro-a26418163/)
- 🐙 GitHub: [JoseGu1llardi](https://github.com/JoseGu1llardi?tab=overview&from=2026-01-01&to=2026-01-10)

---

<div align="center">

**Made with ❤️ and Java ☕**

[⬆ Back to top](#-banking-system---demonstration-of-the-4-oop-pillars)

</div>