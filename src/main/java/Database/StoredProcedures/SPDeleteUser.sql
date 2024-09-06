USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[DeleteUser] @Username Nvarchar(255)
As
Delete from users where Username = @Username
GO


