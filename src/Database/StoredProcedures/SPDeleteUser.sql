USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[DeleteUser]    Script Date: 2024-04-25 19:29:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[DeleteUser] @Username Nvarchar(255)
As
Delete from users where Username = @Username
GO


