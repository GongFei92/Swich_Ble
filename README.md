# Swich_Ble
The Bluetooth module transmits information to the mobile phone's commutation APP, and the commutation APP serves as the first master server to collect the data information collected by the terminal. If the number of terminals is less than 100, the leveling operation is directly performed and the commutation command is sent to the terminal, and may also be on the mobile phone screen. Directly click a terminal to switch to a phase, and finally build a table to save the phase sequence connection information of each terminal commutation switch; if the number of terminals is greater than 100, send the collected data information to the background server (the second main control server) through the http protocol. The background first saves the data information to the database, and then takes out the complex leveling logic operation, calculates the commutation result and then sends it to the commutation APP, and the commutation APP sends the commutation command to the terminal to perform the commutation. To achieve the balance of the three-phase grid.
