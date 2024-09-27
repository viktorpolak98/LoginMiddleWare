USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[CreateAPIKey] @EmailAddress nvarchar(255), @APIKey nvarchar(195), @ValidTo date
As
Begin
	declare @hashed_key varbinary(64);
	declare @ValidFrom date = GETDATE();


	SET @hashed_key = HASHBYTES('SHA2_512', @APIKey);

insert into UserDb.dbo.APIKeys VALUES (@EmailAddress, @hashed_key, @ValidFrom, @ValidTo)
end
GO