USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO





CREATE procedure [dbo].[CheckIfAPIKeyExists] @APIKey nvarchar(195)
As
Begin
	declare @hashed_key nvarchar(195);

	SET @hashed_key = HASHBYTES('SHA2_512', @APIKey);

	SELECT Id, EmailAddress, APIKey, ValidFrom, ValidTo
	FROM dbo.APIKeys
	WHERE @hashed_key = APIKey


end
GO
