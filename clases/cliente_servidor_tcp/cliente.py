import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(), 50001))
s.listen(3)

while True:
    client_socket, address = s.accept()

s.send(123)
s.send(123198731.4927237)
s.send("hola")
