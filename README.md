# Java Bank — OOP Banking System

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/Status-Educational-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Layered-purple?style=flat-square)

> An educational banking system built in pure Java to demonstrate the four pillars of Object-Oriented Programming — **Encapsulation**, **Abstraction**, **Inheritance**, and **Polymorphism** — through a real-world domain.

---

## Table of Contents

- [About](#about)
- [Features](#features)
- [OOP Pillars Applied](#oop-pillars-applied)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Usage Examples](#usage-examples)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---

## About

This project was created as a hands-on study of OOP fundamentals. The banking domain is intentionally familiar so the focus stays on the design patterns rather than the business problem itself.

**Learning objectives:**

- Apply **Encapsulation** to protect internal state and enforce invariants
- Use **Abstraction** to define shared contracts via abstract classes
- Implement **Inheritance** to share and extend behaviour across account types
- Leverage **Polymorphism** so the same operation behaves differently per type

**Additional concepts covered:**

- Value Objects (`CPF`, `Email`) implemented as Java `record`s
- Repository Pattern for decoupled in-memory persistence
- Service Layer to centralise business logic
- Constructor-based Dependency Injection
- `BigDecimal` for precise monetary arithmetic
- `Optional` to represent absence without `null`

---

## Features

### User Management
- Register bank customers with name, CPF (Brazilian tax ID), and email
- CPF validation: strips formatting, enforces 11 digits, rejects all-same-digit sequences
- Email validation via regex, normalised to lowercase
- Email change with duplicate-check guard

### Account Types

| Feature | Checking Account | Savings Account |
|---|---|---|
| Overdraft | Yes — $500.00 default | No |
| Monthly fee | $12.00 (waived if balance ≥ $1,000) | None |
| Monthly yield | No | 0.5% on anniversary date |
| Negative balance | Up to overdraft limit | Not allowed |

### Banking Operations
- Deposit
- Withdrawal (rules enforced per account type)
- Transfer between accounts
- Account statement (full history, date-range, or last N transactions)
- Expenses grouped by transaction type

### Administrative Operations
- Apply yield — savings accounts only
- Charge monthly fee — checking accounts only
- Deactivate account (only when balance is exactly zero)

### Transaction Tracking
Each transaction records: ID, type, amount, previous balance, balance after, timestamp, origin/destination account codes, status, and an authentication code.

**Supported transaction types:** `DEPOSIT`, `WITHDRAW`, `TRANSFER_SENT`, `TRANSFER_RECEIVED`, `BILL_PAYMENT`, `PIX_PAYMENT`, `TED`, `DOC`, `REVERSAL`, `INTEREST`, `FEE`, `YIELD`

**Transaction lifecycle:** `PENDING` → `CONFIRMED` / `CANCELLED`

---

## OOP Pillars Applied

### 1. Encapsulation

Private fields and validation methods prevent the domain from entering an invalid state. All modifications go through controlled public methods.

```java
// domain/vo/CPF.java — value object enforces its own invariants
public record CPF(String value) {
    public CPF(String value) {
        String validCPF = value.replaceAll("\\D", "");
        if (validCPF.length() != 11)
            throw new IllegalArgumentException("Invalid CPF: must contain 11 digits.");
        if (validCPF.matches("(\\d)\\1{10}"))
            throw new IllegalArgumentException("Invalid CPF: repeated digits are not allowed.");
        this.value = validCPF;
    }
}

// domain/model/Account.java — balance is never modified directly from outside
protected void validateAmount(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException("The amount must be greater than zero.");
}
```

### 2. Abstraction

`Account` defines the common contract (deposit, transfer, account code generation) and delegates type-specific behaviour to subclasses via abstract methods.

```java
// domain/model/Account.java
public abstract class Account {
    // Shared, fully-implemented behaviour
    public void deposit(BigDecimal amount) { ... }
    public void transfer(Account destination, BigDecimal amount) { ... }

    // Contract: each subclass must define its own rules
    public abstract void withdraw(BigDecimal amount);
    public abstract BigDecimal calculateMonthlyFee();
}
```

### 3. Inheritance

`CheckingAccount` and `SavingsAccount` extend `Account`, inheriting common fields and methods while adding type-specific logic.

```java
// domain/model/CheckingAccount.java
public class CheckingAccount extends Account {
    private BigDecimal overdraftLimit;        // adds new state
    private static final BigDecimal MONTHLY_FEE = BigDecimal.valueOf(12.0);

    public CheckingAccount(String branch, User holder) {
        super(branch, holder);               // reuses parent constructor
        this.overdraftLimit = BigDecimal.valueOf(500.0);
    }

    // Inherits: deposit(), transfer(), getBalance(), getAccountCode() ...
}
```

### 4. Polymorphism

The same `withdraw` call produces different results at runtime depending on the concrete type — checking accounts allow overdraft, savings accounts do not.

```java
Account checking = new CheckingAccount("0001", user);
Account savings  = new SavingsAccount("0001", user);

checking.withdraw(BigDecimal.valueOf(1800)); // OK — uses overdraft
savings.withdraw(BigDecimal.valueOf(1200));  // throws InsufficientFundsException

// Works uniformly across a mixed collection
List<Account> accounts = List.of(checking, savings);
accounts.forEach(a -> System.out.println(a.calculateMonthlyFee()));
// CheckingAccount → $12.00 (or $0 if balance high enough)
// SavingsAccount  → $0.00
```

---

## Architecture

The project follows a three-layer architecture that separates concerns and keeps the domain free of infrastructure dependencies.

```
┌──────────────────────────────────────────┐
│  JavaBank.java  (entry point / demo)     │
├──────────────────────────────────────────┤
│  application/services/                   │  ← Business logic
│    UserService                           │
│    AccountService                        │
│    TransactionService                    │
├──────────────────────────────────────────┤
│  application/repositories/               │  ← In-memory persistence
│    UserRepository                        │
│    AccountRepository                     │
│    TransactionRepository                 │
├──────────────────────────────────────────┤
│  domain/                                 │  ← Core domain (no dependencies)
│    model/   Account · CheckingAccount    │
│             SavingsAccount · Transaction │
│             User                         │
│    vo/      CPF · Email                  │
│    enums/   TransactionType              │
│             TransactionStatus            │
│    exception/ AccountNotFoundException   │
│               UserNotFoundException      │
│               InsufficientFundsException │
│               EmailAlreadyInUseException │
│               EmailUnchangedException    │
└──────────────────────────────────────────┘
```

### Design Patterns

| Pattern | Where |
|---|---|
| **Repository** | `UserRepository`, `AccountRepository`, `TransactionRepository` |
| **Service Layer** | `UserService`, `AccountService`, `TransactionService` |
| **Template Method** | `Account.transfer()` delegates validation then calls type-specific logic |
| **Value Object** | `CPF` and `Email` as immutable `record`s |
| **Constructor Injection** | All services receive their repositories via constructor |

---

## Project Structure

```
java_bank/
├── src/
│   ├── JavaBank.java                          ← Entry point (simulation demo)
│   │
│   ├── application/
│   │   ├── repositories/
│   │   │   ├── AccountRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   └── UserRepository.java
│   │   └── services/
│   │       ├── AccountService.java
│   │       ├── Transactionservice.java
│   │       └── UserService.java
│   │
│   └── domain/
│       ├── enums/
│       │   ├── TransactionStatus.java
│       │   └── TransactionType.java
│       ├── exception/
│       │   ├── AccountNotFoundException.java
│       │   ├── EmailAlreadyInUseException.java
│       │   ├── EmailUnchangedException.java
│       │   ├── IllegalStateException.java
│       │   ├── InsufficientFundsException.java
│       │   └── UserNotFoundException.java
│       ├── model/
│       │   ├── Account.java
│       │   ├── CheckingAccount.java
│       │   ├── SavingsAccount.java
│       │   ├── Transaction.java
│       │   └── User.java
│       └── vo/
│           ├── CPF.java
│           └── Email.java
│
├── java_bank.iml
└── README.md
```

---

## How to Run

### Prerequisites

- Java JDK 17 or higher
- An IDE such as IntelliJ IDEA (recommended), Eclipse, or VS Code with the Java Extension Pack

### Running in IntelliJ IDEA

1. **Clone the repository**

```bash
git clone https://github.com/JoseGu1llardi/java_bank.git
cd java_bank
```

2. **Open the project**
   - Open IntelliJ IDEA → `File` → `Open` → select the `java_bank` folder
   - Mark `src/` as the Sources Root if not already configured

3. **Run the entry point**
   - Open `src/JavaBank.java`
   - Click the green **Run** arrow next to `main()`

### Running from the command line

```bash
# Compile all sources
javac -d out/production/java_bank $(find src -name "*.java")

# Run the entry point
java -cp out/production/java_bank JavaBank
```

### Expected output (excerpt)

```
========== BANKING SYSTEM ==========
CREATING USERS...
Users created successfully: Jose Guillard, Leticia Castro

CREATING ACCOUNTS...
Checking Account Jose: 000112345678
Saving Account Jose:   000187654321
Checking Account Leticia: 000111223344

MAKING DEPOSIT...
...
PERFORMING TRANSFER...
Jose's Checking Account before: 1500.00
Leticia's balance before: 2000.00
Transfer Completed
Jose's Balance after: 1000.00
Leticia's Balance after: 2500.00
...
APPLYING INCOME - ONLY SAVINGS ACCOUNT
Only savings account can receive yield
```

---

## Usage Examples

### Create users and accounts

```java
UserRepository userRepository = new UserRepository();
AccountRepository accountRepository = new AccountRepository();
TransactionRepository transactionRepository = new TransactionRepository();

UserService userService = new UserService(userRepository);
AccountService accountService = new AccountService(accountRepository, userRepository);
Transactionservice transactionService = new Transactionservice(accountRepository, transactionRepository);

// Create a user — CPF and email are validated automatically
User user = userService.createUser("Jose Guillard", "438-900-898-60", "jose@email.com");

// Create accounts
Account checking = accountService.createCheckingAccount("0001", user.getId());
Account savings  = accountService.createSavingAccount("0001", user.getId());
```

### Perform transactions

```java
// Deposit
transactionService.deposit(checking.getAccountCode(), BigDecimal.valueOf(1500));
transactionService.deposit(savings.getAccountCode(),  BigDecimal.valueOf(1000));

// Withdrawal — checking allows overdraft, savings does not
transactionService.withdraw(checking.getAccountCode(), BigDecimal.valueOf(1800)); // OK
try {
    transactionService.withdraw(savings.getAccountCode(), BigDecimal.valueOf(1200)); // throws
} catch (InsufficientFundsException e) {
    System.out.println("Denied: " + e.getMessage());
}

// Transfer
transactionService.transfer(
    checking.getAccountCode(),
    savings.getAccountCode(),
    BigDecimal.valueOf(500)
);
```

### Query statements

```java
// Full history (sorted newest-first)
List<Transaction> history = transactionService.getStatement(checking.getAccountCode());
history.forEach(System.out::println);

// Last 5 transactions
List<Transaction> recent = transactionService.getRecentStatement(checking.getAccountCode(), 5);

// Date-range statement
List<Transaction> period = transactionService.getStatement(
    checking.getAccountCode(),
    LocalDateTime.of(2025, 1, 1, 0, 0),
    LocalDateTime.of(2025, 12, 31, 23, 59)
);

// Expenses grouped by type
Map<TransactionType, BigDecimal> breakdown =
    transactionService.getStatementExpensesByType(checking.getAccountCode());
```

### Administrative operations

```java
// Apply monthly fee to checking accounts (only charged if balance < $1,000)
transactionService.chargeFee(checking.getAccountCode());

// Apply monthly yield to savings accounts (0.5%, once per anniversary date)
transactionService.applyYield(savings.getAccountCode());

// Deactivate an account (balance must be zero)
accountService.deactivateAccount(checking.getAccountCode());
```

---

## Future Enhancements

- **Authentication** — credential management with password hashing and login attempt limits
- **Premium account type** — `PremiumCheckingAccount` with higher fee-waiver threshold
- **Daily limits** — per-account daily withdrawal and transfer caps
- **Real persistence** — replace `HashMap` repositories with JDBC or JPA
- **Observer / event system** — notify on low balance, large transactions, etc.
- **Unit tests** — JUnit 5 test suite covering domain logic and services
- **REST API** — expose operations via Spring Boot controllers

---

## Contributing

Contributions are welcome! This is an educational project and improvements are always appreciated.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

**Contribution ideas:** unit tests, new account types, Javadoc improvements, design pattern refactors, build system setup (Maven/Gradle).

---

## License

This project is licensed under the MIT License. See the [LICENSE](java_bank/LICENSE) file for details.

---

## Author

**José Guillardi**

- Email: junior11_junior@hotmail.com
- LinkedIn: [José Wellington Ribeiro](https://www.linkedin.com/in/jos%C3%A9-wellington-ribeiro-a26418163/)
- GitHub: [JoseGu1llardi](https://github.com/JoseGu1llardi)

---

<div align="center">

Made with Java

[Back to top](#java-bank--oop-banking-system)

</div>