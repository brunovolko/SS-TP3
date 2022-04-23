import numpy as np
import pandas
from matplotlib import pyplot as plt



def fun(x,m):
    return m*x
df = pandas.read_csv('avgs.csv')



y = np.array(df['avgs'])
x = np.array(df['energia'])
it = 0
eme=0.01

errors=[]
emes=[]
while eme < 80:

    error=0.0
    for i in range(len(y)):
        error+=(y.item(i) - fun(x.item(i),eme) )**2
    eme+=0.001
    errors.append(error)
    emes.append(eme)

min = errors.index(min(errors))
print(min)
print(emes[53245])
plt.xlabel('c')
plt.ylabel('E(c)')
plt.plot(emes,errors)
plt.savefig("error_regresiones.png")
