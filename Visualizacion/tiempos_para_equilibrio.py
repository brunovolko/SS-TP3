import pandas
from matplotlib import pyplot as plt
import numpy as np

id = 0
fig, ax = plt.subplots()
while True:

    try:
        id += 1
        df = pandas.read_csv('time_to_balance'+str(id)+'.csv')
        avg = df.mean()
        dev = df.std()
        plt.errorbar(id, avg, yerr=dev, fmt='o', label='Tiempo para equilibrio')

    except FileNotFoundError:
        break
list = ['N=100\nd=0.02', 'N=100\nd=0.03', 'N=100\nd=0.045', 'N=300\nd=0.02', 'N=300\nd=0.03', 'N=300\nd=0.045']
ax.set_xticks(range(1, id))
ax.set_xticklabels(list)
plt.ylabel('Tiempo hasta equilibrio')
plt.savefig('tiempos_para_equilibrio.png')