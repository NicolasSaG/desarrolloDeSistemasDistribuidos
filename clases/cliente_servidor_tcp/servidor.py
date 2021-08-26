import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(), 50001))
s.listen(3)

while True:
    client_socket, address = s.accept()
