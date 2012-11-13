CREATE DATABASE VLocation
GO

USE VLocation
GO

CREATE TABLE Categories(
	ID INT IDENTITY PRIMARY KEY,
	Name CHAR(100),
	CreatedDate DATETIME
)
GO
	
INSERT INTO Categories(Name, CreatedDate) VALUES('Food', GETDATE())
INSERT INTO Categories(Name, CreatedDate) VALUES('Shopping', GETDATE())
INSERT INTO Categories(Name, CreatedDate) VALUES('ATM', GETDATE())
INSERT INTO Categories(Name, CreatedDate) VALUES('School', GETDATE())
INSERT INTO Categories(Name, CreatedDate) VALUES('Market', GETDATE())

SELECT * FROM Categories