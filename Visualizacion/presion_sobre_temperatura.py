import pandas
import numpy as np
from matplotlib import pyplot as plt

static_file = open('static_input.txt', 'r')
df = pandas.read_csv('pressure-results.csv')
x = np.linspace(-0,30,1000)
y = 53.25599999996658*x
radius = float(static_file.readline())
mass = float(static_file.readline())
width = float(static_file.readline())
height = float(static_file.readline())
groove = float(static_file.readline())
cant = int(static_file.readline())
vel = float(static_file.readline())


limit = len(df.columns)
i=1
while i<=limit:
    ax = plt.gca()
    ax.set_ylim([0,18])
    ax.set_xlim([0,0.4])

  #  plt.plot(x,y,linewidth=0.1)

    key = 'p'+str(i)
    plt.plot(cant*vel*vel*mass/2, df[key].mean())
    plt.errorbar(cant*vel*vel*mass/2, df[key].mean(), df[key].std(), linestyle='None', marker='')

    energia = cant*vel*vel*mass/2
    print(str(df[key].mean())+ ',' + str(energia))
    vel += 0.01
    i+=1

plt.xlabel('Temperatura(J)')
plt.ylabel('Presion(N/m)')

plt.savefig('presion_sobre_temperatura.png')
