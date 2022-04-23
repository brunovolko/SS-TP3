static_file = open('static_input.txt', 'r')
dynamic_file = open('dynamic_output.txt', 'r')

radio = float(static_file.readline())
masa = float(static_file.readline())
width = float(static_file.readline())
height = float(static_file.readline())
groove = float(static_file.readline())
cant = int(static_file.readline())
v0 = float(static_file.readline())

TIME_GAP = 0.5

f = open("animation_input.xyz", "w")
eventos = list()
t = 0
prev_era_t = False
while True:
    line = dynamic_file.readline()
    if not line:
        aux = {
            'tiempo': tiempo,
            'data': evento_actual
        }
        eventos.append(aux)
        break
    if line[0] == 't':
        if t > 0:
            aux = {
                'tiempo': tiempo,
                'data': evento_actual
            }
            eventos.append(aux)
        evento_actual = list()
        t += 1
        prev_era_t = True
    elif prev_era_t:
        tiempo = float(line[:-1])
        prev_era_t = False
    else:
        data = line[:-1].split(' ')
        aux = {
            'x': float(data[0]),
            'y': float(data[1]),
            'vx': float(data[2]),
            'vy': float(data[3])
        }
        evento_actual.append(aux)

def guardar_evento(evento):
    f.write(str(len(evento)) + "\n\n")
    for data in evento:
        f.write(str(data['x']) + " " + str(data['y']) + " " + str(data['vx']) + " " + str(data['vy']) + " " + str(radio) + " " + str(masa) + "\n")
    

prev_time = -1.0
for idx in range(len(eventos)):
    tiempo = eventos[idx]['tiempo']
    if(prev_time < 0.0):
        prev_time = tiempo
        guardar_evento(eventos[idx]['data'])
    else:
        if(tiempo - prev_time >= TIME_GAP):
            guardar_evento(eventos[idx]['data'])
            prev_time = prev_time + TIME_GAP
        
    
f.close()