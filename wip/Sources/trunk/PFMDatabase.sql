CREATE DATABASE PFMDatabase
GO

USE PFMDatabase
GO

CREATE TABLE "User"(
	ID INT IDENTITY PRIMARY KEY,
	UserName VARCHAR(100),
	LastSync DATETIME
)
GO
	
CREATE TABLE BorrowLend(
	ID BIGINT,
	UserID INT,
	DebtType nvarchar(100),
	"Money" Float,
	InterestType nvarchar(100),
	InterestRate INT,
	StartDate Date,
	ExpiredDate Date,
	PersonName NVARCHAR(100),
	PersonPhone NVARCHAR(100),
	PersonAddress NVARCHAR(100),
	IsDeleted INT, 
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (UserID) REFERENCES "User"(ID)
)
GO

CREATE TABLE Category(
	ID BIGINT,
	UserID INT,
	Name NVARCHAR(100),
	UserColor NVARCHAR(100),
	IsDeleted INT, 
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (UserID) REFERENCES "User"(ID),
)
GO

CREATE TABLE Schedule(
	ID BIGINT,
	UserID INT,
	Budget Float,
	"Type" int,
	StartDate Date,
	EndDate Date,
	IsDeleted INT, 
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (UserID) REFERENCES "User"(ID)
)
GO

CREATE TABLE ScheduleDetail(
	ID BIGINT,
	UserID INT,
	Budget Float,
	CategoryID bigint,
	ScheduleID bigint,
	IsDeleted INT, 
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (ScheduleID, UserID) REFERENCES "Schedule"(ID, UserID),
	FOREIGN KEY (CategoryID, UserID) REFERENCES "Category"(ID, UserID)
)
GO

CREATE TABLE "Entry"(
	ID BIGINT,
	UserID INT,
	"Date" Date,
	IsDeleted INT, 
	"Type" INT,
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (UserID) REFERENCES "User"(ID),
)
GO

CREATE TABLE "EntryDetail"(
	ID BIGINT,
	UserID INT,
	CategoryID BIGINT,
	Name NVARCHAR(100),
	"Money" Float,
	EntryID bigINT,
	IsDeleted INT, 
	CreatedDate DATETIME,
	ModifiedDate DATETIME,
	LastSync DATETIME,
	PRIMARY KEY (ID, UserID), 
	FOREIGN KEY (EntryID, UserID) REFERENCES "Entry"(ID, UserID),
	FOREIGN KEY (CategoryID, UserID) REFERENCES "Category"(ID, UserID)
)
GO