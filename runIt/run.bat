:: Only change id and password as per your Rally account. 
:: You can define rule filename as 5th parameter(it is optional with this version, in that case it will look for rall.rules in default directory).
:: 24 is an interval. Every 24 hours this will get fired
:: You can also define hour of the day, like 9AM or 10PM instead of internal time. This way you can schedule process to start at that time and to run every 24 hours after it. Just make sure rule file is all good because you can only find issue when it executes first time.
java -jar sqer.jar 0 rallyid rallypassword 24