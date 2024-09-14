USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[CreateAPIUser] @EmailAddress nvarchar(255)
As
Begin
insert into UserDb.dbo.APIUsers VALUES (@EmailAddress)
end
GO