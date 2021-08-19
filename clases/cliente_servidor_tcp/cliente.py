import socket

try:
	conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	conn.connect(("localhost", 50000))

	conn.send(123)
	conn.send(123198731.4927237)
	con.send("hola")

	conn.close()
except Exception as e:
	print("Error con el socket: {}".format(e))


