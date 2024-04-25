USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[CreateUser]    Script Date: 2024-04-25 19:29:48 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[CreateUser] @Username nvarchar(255), @Password nvarchar(255)
As
Begin
	declare @hashed_password varbinary(64);
	declare @Salt UNIQUEIDENTIFIER = newid();


	SET @hashed_password = HASHBYTES('SHA2_512', CONCAT(@Password, CAST(@salt AS NVARCHAR(36))));

insert into UserDb.dbo.users VALUES (@Username, @hashed_password, @Salt)
end
GO


