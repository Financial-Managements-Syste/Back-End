
CREATE TABLE Users (
    user_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE Users
DROP COLUMN last_sync_at;

ALTER TABLE USERS ADD (LAST_SYNC_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE Categories (
    category_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR2(50) NOT NULL UNIQUE,
    category_type VARCHAR2(20) NOT NULL,
    description VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_category_type CHECK (category_type IN ('Income', 'Expense'))
);

DROP TABLE Categories;

DELETE FROM Categories;
TRUNCATE TABLE CATEGORIES;


DELETE FROM SavingsGoals;
ALTER TABLE SavingsGoals MODIFY goal_id GENERATED AS IDENTITY (START WITH 1);


CREATE TABLE Transactions (
    transaction_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,                
    user_id        NUMBER NOT NULL,
    category_id    NUMBER NOT NULL,
    amount         NUMBER(10,2) NOT NULL,
    transaction_type VARCHAR2(20) NOT NULL,
    transaction_date DATE NOT NULL,
    description    VARCHAR2(255),
    payment_method VARCHAR2(255),                      
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      

    CONSTRAINT fk_trans_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_trans_category FOREIGN KEY (category_id) REFERENCES Categories(category_id),
    CONSTRAINT chk_trans_type CHECK (transaction_type IN ('Income', 'Expense')),
    CONSTRAINT chk_trans_amount CHECK (amount > 0)
);

DROP TABLE Transactions;

CREATE TABLE Budgets (
    budget_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    category_id NUMBER NOT NULL,
    budget_name VARCHAR2(255),
    budget_description VARCHAR2(255),
    budget_amount NUMBER(10,2) NOT NULL,
    budget_period VARCHAR2(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES Categories(category_id),
    CONSTRAINT chk_budget_period CHECK (budget_period IN ('Daily', 'Weekly', 'Monthly', 'Yearly')),
    CONSTRAINT chk_budget_amount CHECK (budget_amount > 0),
    CONSTRAINT chk_budget_dates CHECK (end_date > start_date)
);


DROP TABLE Budgets;

CREATE TABLE SavingsGoals (
    goal_id NUMBER PRIMARY KEY GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    goal_name VARCHAR2(100) NOT NULL,
    target_amount NUMBER(10,2) NOT NULL,
    current_amount NUMBER(10,2) DEFAULT 0,
    target_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(20) DEFAULT 'Active',
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_goal_target CHECK (target_amount > 0),
    CONSTRAINT chk_goal_current CHECK (current_amount >= 0),
    CONSTRAINT chk_goal_status CHECK (status IN ('Active', 'Completed', 'Cancelled'))
);


DROP TABLE SavingsGoals;
CASCADE CONSTRAINTS;



CREATE TABLE SyncMetadata (
    sync_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    table_name VARCHAR2(50) NOT NULL,
    last_sync_timestamp TIMESTAMP NOT NULL,
    records_synced NUMBER DEFAULT 0,
    sync_status VARCHAR2(20) DEFAULT 'Success',
    error_message VARCHAR2(500),
    CONSTRAINT fk_sync_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_sync_status CHECK (sync_status IN ('Success', 'Failed', 'Partial'))
)

CREATE INDEX idx_trans_user ON Transactions(user_id);
CREATE INDEX idx_trans_date ON Transactions(transaction_date);
CREATE INDEX idx_trans_category ON Transactions(category_id);
CREATE INDEX idx_budget_user ON Budgets(user_id);
CREATE INDEX idx_savings_user ON SavingsGoals(user_id);




INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (1, 1, 2, 5000.00, 'Income', TO_DATE('2025-10-01', 'YYYY-MM-DD'), 'October Salary', 'Bank Transfer');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (2, 1, 3, 150.50, 'Expense', TO_DATE('2025-10-02', 'YYYY-MM-DD'), 'Weekly groceries', 'Credit Card');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (3, 1, 4, 45.00, 'Expense', TO_DATE('2025-10-03', 'YYYY-MM-DD'), 'Uber rides', 'Debit Card');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (4, 1, 8, 1200.00, 'Expense', TO_DATE('2025-10-01', 'YYYY-MM-DD'), 'Monthly rent', 'Bank Transfer');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (5, 2, 2, 4500.00, 'Income', TO_DATE('2025-10-01', 'YYYY-MM-DD'), 'October Salary', 'Bank Transfer');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (6, 2, 3, 200.00, 'Expense', TO_DATE('2025-10-02', 'YYYY-MM-DD'), 'Grocery shopping', 'Cash');

INSERT INTO Transactions (transaction_id, user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES 
 (7, 2, 6, 50.00, 'Expense', TO_DATE('2025-10-03', 'YYYY-MM-DD'), 'Netflix subscription', 'Credit Card');




COMMIT;


INSERT INTO Users (username, email, password_hash) VALUES ('Sithil', 'sithil@gmail.com', 'hashed_password_123');
INSERT INTO Users (username, email, password_hash) VALUES ('semitha', 'Semitha@gmail.com', 'hashed_password_456');
INSERT INTO Users (username, email, password_hash) VALUES ('manuli', 'mauli@gmail.com', 'hashed_password_789');

COMMIT;

SELECT 'BEFORE SYNC - ORACLE TRANSACTION COUNT' as status FROM DUAL;
SELECT COUNT(*) as total_transactions FROM Transactions;

INSERT INTO Transactions 
(user_id, category_id, amount, transaction_type, transaction_date, description, payment_method)
VALUES (1, 1, 5000.00, 'Income', TO_DATE('2025-10-01', 'YYYY-MM-DD'), 'October Salary', 'Bank Transfer')


INSERT INTO SyncMetadata (user_id, table_name, last_sync_timestamp, records_synced, sync_status)
VALUES (1, 'Transactions', CURRENT_TIMESTAMP, 1, 'Success');

SELECT COUNT(*) FROM Transactions;