USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[CreateAPIKey]    Script Date: 2024-09-21 12:00:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[CreateAPIKey] @APIUser nvarchar(255), @APIKey nvarchar(195), @ValidTo date
As
Begin
	declare @hashed_key varbinary(64);
	declare @Salt UNIQUEIDENTIFIER = newid();
	declare @ValidFrom date = GETDATE();


	SET @hashed_key = HASHBYTES('SHA2_512', CONCAT(@APIKey, CAST(@salt AS NVARCHAR(36))));

insert into UserDb.dbo.APIKeys VALUES (@APIUser, @hashed_key, @Salt, @ValidFrom, @ValidTo)
end
GO