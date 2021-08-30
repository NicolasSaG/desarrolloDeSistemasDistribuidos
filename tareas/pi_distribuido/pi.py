
res = 0
presicion = 2000000
for i in range(0, presicion):
    res = res + 4/(1+4*i)

for i in range(0, presicion):
    res = res - 4/(3+4*i)

print(res)

res = 0
for j in range(1, 5):
    suma = 0
    for i in range(0, 1000000):
        suma = (4 / (8 * i + 2 * (j - 2) + 3)) + suma
    suma = -suma if j % 2 == 0 else suma
    res += suma
    print(suma)
print(res)
