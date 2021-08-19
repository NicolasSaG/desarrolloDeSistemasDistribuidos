import socket

try:
	conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	conn.connect(("localhost", 50000))

	conn.close()
except Exception as e:
	print("Error con el socket: {}".format(e))


