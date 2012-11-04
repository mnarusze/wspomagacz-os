'This script is adds users from the Windows NT DS
'via dsadd. The script reads an EXCEL spreadsheet that contains a page 
'of users to add.  


' ---------------------------------------------------------
' Configuration Variables
' ---------------------------------------------------------
ldapBase			= "DC=KASKLAB,DC=KASK,DC=ETI,DC=PG,DC=GDA,DC=PL"
domainName			= "KASKLAB"
defaultShell			= "/bin/bash"
defaultGidNumber		= "10000"		' The primary group ID for new users
defaultBaseHomeDir		= "/macierz/home/"
maxsetIDTries			= 20			' Try to set the unix attributes this many times before quitting.
defaultBaseID			= "9999"		' The ID to start (one is added to this) at if no IDs are currently defined.



Dim oXL
Dim u
Dim c
Dim root
Dim ou
Dim TextXL
Dim CRLF
dim oArgs
Dim grp


' ---------------------------------------------------------
' Check that we received a XLS file as a command line 
' parameter.
' ---------------------------------------------------------    
'Get the command line args
set oArgs=wscript.arguments

CRLF = Chr(13) & Chr(10)
  
'If no command line arguments provided, prompt for file containing users to add/delete
If oArgs.Count = 0 Then
	WScript.Echo "Please pass a path to Excel spreadsheet containing users to add to AD"
	WScript.Echo "cscript AddUsers.vbs <path to XLS file>"
	WScript.Quit(1)
'Else file containing users is the first argument
Else
	TextXL = oArgs.item(0)
End If

' ---------------------------------------------------------
' Setup ADSI ADO Connection to Active Directory
' ---------------------------------------------------------
set objConnection 		= CreateObject("ADODB.Connection")
objConnection.Provider 		= "ADsDSOObject"
objConnection.Open "Active Directory Provider"

ParseXLSFile(TextXL)

' ---------------------------------------------------------
' Normal exit
' --------------------------------------------------------- 
WScript.Quit(0)




' ---------------------------------------------------------
' Parses given XLS file and adds users to ActiveDirectory
' using "dsadd user" command
' ---------------------------------------------------------
Function ParseXLSFile(ByVal TextXL)
    'We will use ou to control loop, so set initial value to null
    ou = ""
   
    'Start EXCEL and display it to the user
    Set oXL = WScript.CreateObject("EXCEL.application")
    oXL.Visible = True

    'Open the workbook passed in the command line
    oXL.workbooks.open TextXL

    'Activate the Add page 
    oXL.sheets("Add").Activate

    'Put the cursor in the starting cell and read the DS root
    oXL.ActiveSheet.range("A2").Activate ' this cell has the DS root in it

    'Show it to the user
    WScript.Echo "Root DN: " & oXL.activecell.Value
  
    'This is the starting point in the ds
    root = oXL.activecell.Value

    'Step to the next row
    oXL.activecell.offset(1, 0).Activate

    'Until we run out of rows
    Do While oXL.activecell.Value <> ""
      
        'if the requested OU is a new one...
        If oXL.activecell.Value <> ou Then
            'Pick up the OU name...
            ou = oXL.activecell.Value

            'Compose the ADSI path...
            s = "LDAP://" + ou+"," + root

            'Show it to the user...
	    WScript.Echo "Current DN: " & s

            'And get the object
            Set c = GetObject(s)
        End If

        'Compose the user common name name from first and last names...
        uname = "CN=" + oXL.activecell.offset(0, 1).Value + " " + oXL.activecell.offset(0, 2).Value + "," + ou + "," + root

	'Compose user name form first name and last name
	name = oXL.activecell.offset(0, 1).Value + " " + oXL.activecell.offset(0, 2).Value

	'get First Name
	givenName = oXL.activecell.offset(0, 1).Value

	'get Surname
	sn = oXL.activecell.offset(0, 2).Value

	'get sAMAccountName
	sAMAccountName = oXL.activecell.offset(0, 3).Value

	'get userPrincipalName
	userPrincipalName = oXL.activecell.offset(0, 3).Value + oXL.activecell.offset(0, 4).Value

	'get description
	description = oXL.activecell.offset(0, 6).Value

	'get e-mail
	mail = oXL.activecell.offset(0, 7).Value

	'get password
	password = oXL.activecell.offset(0, 5).Value

	'default groups
	linux_dialout = "CN=linux_dialout,CN=Users,DC=kasklab,DC=kask,DC=eti,DC=pg,DC=gda,DC=pl"
	linux_disk = "CN=linux_disk,CN=Users,DC=kasklab,DC=kask,DC=eti,DC=pg,DC=gda,DC=pl"
	linux_vboxusers = "CN=linux_vboxusers,CN=Users,DC=kasklab,DC=kask,DC=eti,DC=pg,DC=gda,DC=pl"
	linux_video = "CN=linux_video,CN=Users,DC=kasklab,DC=kask,DC=eti,DC=pg,DC=gda,DC=pl"


'	WScript.Echo "UName: " & uname
'	WScript.Echo "Name: " & name
'	WScript.Echo "givenName: " & givenName
'	WScript.Echo "sn: " & sn
'	WScript.Echo "sAMAccountName: " & sAMAccountName
'	WScript.Echo "userPrincipalName: " & userPrincipalName
'	WScript.Echo "e-mail: " & mail
'	WScript.Echo "Password: " & password

	command = "dsadd user """ + uname + """ -desc """ + description + """ -display """ + name + """ -samid " + sAMAccountName + " -upn " + userPrincipalName + " -fn """ + givenName + """ -ln """ + sn + """ -pwd " + password + " -email " + mail + " -profile \\apl01\homes\profiles\" + sAMAccountName + " -hmdrv H: -hmdir \\apl01\homes\" + sAMAccountName + " -mustchpwd yes -canchpwd yes -pwdneverexpires no -memberof """ + linux_dialout + """ """ + linux_disk + """ """ + linux_vboxusers + """ """ + linux_video + """"
'	WScript.Echo command

	ExecuteShellProgram command

	assignUNIXAttributes(sAMAccountName)

        'Step to the next user...
        oXL.activecell.offset(1, 0).Activate   'Next row
    Loop


    'Done.  close excel spreadsheet
    oXL.application.quit
End Function


' ---------------------------------------------------------
' Executes an externall command, waits for it to complete
' and collects it's output
' ---------------------------------------------------------
Function ExecuteShellProgram(ByVal sFileName)
	Dim poShell
	Dim poProcess
	Dim iStatus

	Set poShell = CreateObject("WScript.Shell")
	Set poProcess = poShell.Exec(sFileName)

	'Check to see if we started the process without error

	if ((poProcess.ProcessID=0) and (poProcess.Status=1)) then
		Err.Raise vbObjectError,,"Failed executing process"
	end if

	'Now loop until the process has terminated, and pull out
	'any console output

	Do
		'Get current state of the process
		iStatus = poProcess.Status

		'Forward console output from launched process to ours
		WScript.StdOut.Write poProcess.StdOut.ReadAll()
		WScript.StdErr.Write poProcess.StdErr.ReadAll()

		'Did the process terminate?
		if (iStatus <> 0) then
			Exit Do
		end if
	Loop

	'Return the exit code
	ExecuteShellProgram = poProcess.ExitCode
End Function


Function assignUNIXAttributes(objectname)
	' ---------------------------------------------------------
	'  Set objectclass to "user" - for possible generalization
	' ---------------------------------------------------------
	objectclass	= "user"
	
	' ---------------------------------------------------------
	' Set the UNIX ID. 
	' 
	' Check if the ID was set correctly and if not retry
	' up the ammount of times set in maxsetIDTries.
	' ---------------------------------------------------------
	setIDTries = 0
	While( (setID(objectname, objectclass, Null, Null, Null) <> 0) AND (setIDTries < maxsetIDTries))
		setIDTries = setIDTries + 1
		If setIDTries = maxsetIDTries Then	
			WScript.Echo "ERROR: UNIX ID set was not unique, retrying again for  " & objectname & "."
			WScript.Quit(1)
		End If
	WEnd
End Function


' ---------------------------------------------------------
' SetID for User
' ---------------------------------------------------------
function setID(objectname, objectclass, objectID, homeDirectory, loginShell)

	' ---------------------------------------------------------
	' Retrive the LDAP distinguishedname for this object
	' ---------------------------------------------------------
	objectDN = returnProperty("distinguishedName", "*", "sAMAccountName", objectname, "")
'	WScript.Echo objectDN
	If objectDN = "ERROR" Then
		WScript.Echo "The object " & objectname & " was not found in Active Directory"
		WScript.Quit(1)
	ElseIf IsArray(objectDN ) Then
		WScript.Echo "I found more then one object in Active Directory with this name"
		WScript.Quit(1)	
	End If
	objectDN = Replace(objectDN, "/", "\/")
		
	' ---------------------------------------------------------
	' Retrive the current highest UNIX UID that is defined
	' and add one to this value for the current user
	' ---------------------------------------------------------
	If IsNull(objectID) Then
		maxUID 		= returnMaxID(objectclass)
		objectID	= maxUID + 1
	End If
'	WScript.Echo maxUID & " " & objectID
	
	If IsNull(homeDirectory) AND objectclass = "user" Then
		homeDirectory 	= defaultBaseHomeDir & objectname	
	End If
'	WScript.Echo homeDirectory

	If objectclass = "user" Then
		gidNumber 	= defaultGidNumber
	End If
	
	' ---------------------------------------------------------
	' Check the login Shell
	' ---------------------------------------------------------
	If IsNull(loginShell) Then
		loginShell = defaultShell
	End If
		
	' ---------------------------------------------------------
	' Setup the AD object
	' ---------------------------------------------------------
	set adObj = GetObject("LDAP://" & objectDN)
		
	' ---------------------------------------------------------
	' Set the UNIX attributes
	' ---------------------------------------------------------
	if objectclass = "user" Then

		' ---------------------------------------------------------
		' Check to see if UID is already assigned
		' ---------------------------------------------------------
		verifyUniqueID = returnProperty("uidNumber", objectclass, "uid", objectname, "")
'		WScript.Echo verifyUniqueID & " " & objectID

		If trim(verifyUniqueID) <> "ERROR" Then
			' ---------------------------------------------------------
			' Normal exit
			' ---------------------------------------------------------
			WScript.Echo "WARNING: UNIX ID for " & objectname & " already assigned"
			setID = 0
			WScript.Quit(0)
		Else
			adObj.Put "gidNumber", 		gidNumber
			adObj.Put "unixHomeDirectory", 	homeDirectory
			adObj.Put "loginShell", 	loginShell
			adObj.Put "msSFU30Name", 	objectname
			adObj.Put "uid",	 	objectname
			adObj.Put "msSFU30NisDomain", 	LCase(domainName)
			adObj.Put "uidNumber", 		objectID
			adObj.SetInfo
			verifyUniqueID = returnProperty("uidNumber", objectclass, "uidNumber", objectID, "")



		End If		
	ElseIf objectclass = "group" Then
		adObj.Put "msSFU30Name", 	objectname
		adObj.Put "msSFU30NisDomain", 	LCase(domainName)
		adObj.Put "gidNumber", 		objectID
		adObj.SetInfo
		
		verifyUniqueID = returnProperty("GidNumber", objectclass, "GidNumber", objectID, "")		
	Else
		WScript.Echo "ERROR: The objectclass passed (" & objectclass & ") was not recognized."
		WScript.Quit(1)
	End If
	
	' ---------------------------------------------------------
	' Check to see if this UID is unique
	' ---------------------------------------------------------
	If trim(verifyUniqueID) = trim(objectID) Then
		' ---------------------------------------------------------
		' Normal exit
		' ---------------------------------------------------------
		WScript.Echo "SUCCESS: UNIX ID for " & objectname & " is " & objectID
		setID = 0
	ElseIf InStr(verifyUniqueID, ":") <> 0 Then
		' ---------------------------------------------------------
		' ERROR: Unix UID NOT Unique
		' ---------------------------------------------------------
		WScript.Echo "ERROR: UNIX ID of " & objectname & " for " & objectID & " is NOT unique."
		setID = 1
	Else
		' ---------------------------------------------------------
		' Error performing AD Search
		' ---------------------------------------------------------
		WScript.Echo "ERROR: Error searching Active Directory for unique UNIX ID "
		setID = "ERROR"
	End If
End Function

' ---------------------------------------------------------
' Return a LDAP property from Active Directory.
' ---------------------------------------------------------
function returnProperty(property, objectclass, searchProperty, searchValue, filtercomp)
	set objCommand 				= CreateObject("ADODB.Command")
	Set objCommand.ActiveConnection 	= objConnection

	objCommand.CommandText 			= "SELECT " & property & " FROM 'LDAP://" & ldapBase & "' " & _
						  "WHERE objectClass='" & objectclass & "' AND " & filtercomp & " " & searchProperty & "='" & searchValue & "'"
'	WScript.Echo "RET: " & objCommand.CommandText
	objCommand.Properties("Timeout") 	= 30
	objCommand.Properties("Time Limit") 	= 30
	objCommand.Properties("Cache Results") 	= True
	objCommand.Properties("Page Size") 	= 999

	set objRecordset 			= objCommand.Execute

'	WScript.Echo "RET: " & objRecordset.RecordCount
	
	If objRecordset.RecordCount = 1 Then
		objRecordset.MoveFirst
		returnProperty = objRecordSet.Fields(property)
	ElseIf objRecordSet.RecordCount > 1 Then
		objRecordset.MoveFirst
		returnPropetry = ""
		Do Until objRecordSet.EOF			
			returnProperty = objRecordSet.Fields(property)  & ":" &  returnProperty
			objRecordSet.MoveNext
		Loop		
	Else
		returnProperty = "ERROR"
	End If
End Function

' ---------------------------------------------------------
' Return the current highest UNIX UID
' ---------------------------------------------------------
function returnMaxID(objectclass)
	set objCommand 			= CreateObject("ADODB.Command")
	Set objCommand.ActiveConnection = objConnection
	
	If objectclass = "user" Then
		idnumber = "uidNumber"
	ElseIf objectclass = "group" Then
		idnumber = "gidNumber"
	Else
		WScript.Echo "ERROR: The objectclass " & objectclass & " passed was not recognized."
		WScript.Quit(1)
	End If

	objCommand.CommandText 		= "SELECT " & idnumber & ", sAMAccountName FROM 'LDAP://" & ldapBase & "' " & _
			 	  	  "WHERE objectClass='" & objectclass & "' AND " & idnumber & "='*' ORDER BY " & idnumber & " DESC"

'	WScript.Echo "MAX: " & objCommand.CommandText

	objCommand.Properties("Timeout") 	= 30
	objCommand.Properties("Time Limit") 	= 30
	objCommand.Properties("Cache Results") 	= False
'	objCommand.Properties("Size Limit") 	= 100

	set objRecordset 			= objCommand.Execute

'	WScript.Echo "MAX: " & objRecordset.RecordCount
'	WScript.Echo objRecordSet.Fields(idnumber)

	If objRecordset.RecordCount > 0 Then
		objRecordset.MoveFirst
		returnMaxID = objRecordSet.Fields(idnumber)
	Else
		' ---------------------------------------------------------
		' If no IDs are found, assume that no IDS
		' are currently defined and use the default
		' ID
		' ---------------------------------------------------------
		returnMaxID = defaultBaseID
	End If
End Function