import json
import os
import sys
from datetime import datetime
import irsdk
import time
import pandas as pd
import tkinter as tk
import threading
import requests
from irsdk import TrackWetness

class State:
    ir_connected = False
    last_car_setup_tick = -1

def check_iracing():
    if state.ir_connected and not (ir.is_initialized and ir.is_connected):
        state.ir_connected = False
        state.last_car_setup_tick = -1
        ir.shutdown()
        print('irsdk disconnected')
    elif not state.ir_connected and ir.startup() and ir.is_initialized and ir.is_connected:
        state.ir_connected = True
        print('irsdk connected')

def loop():
    global currentLap, topSpeed, fuelStart, initialized
    while running:
        check_iracing()
        if state.ir_connected:
            newLap = ir['Lap']
            currentSpeed = ir['Speed']
            idx = ir['DriverInfo']['DriverCarIdx']

            topSpeed = max(topSpeed, round(currentSpeed * 3.6, 2))
            if not initialized:
                currentLap = newLap
                fuelStart = ir['FuelLevel']
                initialized = True
                print(f"Initialized on lap {currentLap}.")
                getactualsession()
                continue

            if ir['LapDistPct'] >= 0.999:
                time.sleep(3)
                lapTime = round(ir['LapLastLapTime'], 3)
                fuelUsed = round(fuelStart - ir['FuelLevel'], 2)
                trackWetnessValue = ir['TrackWetness']

                formattedLapTime = (
                    convertToMinutes(lapTime) if lapTime >= 60 else f"{lapTime:.3f}"
                )

                stintInfo[currentLap] = {
                    'driver': ir['DriverInfo']['Drivers'][idx]['UserName'],
                    'track': ir['WeekendInfo']['TrackDisplayShortName'],
                    'car': ir['DriverInfo']['Drivers'][idx]['CarScreenName'],
                    'trackState': get_name_from_value(TrackWetness, trackWetnessValue),
                    'lapTimeNumeric': lapTime,
                    'lapTimeFormatted': formattedLapTime,
                    'topSpeed': topSpeed,
                    'session': getactualsession(),
                    'fuelTank': round(ir['FuelLevel'], 2),
                    'fuelUsed': fuelUsed,
                    'trackTemp': round(ir['TrackTempCrew'], 2),
                    'waterTemp': round(ir['WaterTemp'], 2),
                    'oilTemp': round(ir['OilTemp'], 2),
                    'createdAt': datetime.today().strftime('%Y-%m-%d %H-%M-%S')
                }

                sendData(stintInfo[currentLap])
                fuelStart = ir['FuelLevel']
                topSpeed = 0
                currentLap += 1
        time.sleep(1 / 60)

def convertToMinutes(volta):
    minutes_seconds, miliseconds = divmod(volta, 1)
    minutes = int(minutes_seconds // 60)
    seconds = int(minutes_seconds % 60)
    miliseconds = int(miliseconds * 1000)
    return f'{minutes}:{seconds:02}.{miliseconds:03}'

def getactualsession():
    session = ""
    for i in ir['SessionInfo']['Sessions']:
        if i['ResultsOfficial'] == 0:
            session = i['SessionName']
            break
    return session

def sendData(sim_data):
    json_data = json.dumps(sim_data)
    print(json_data)
    req = requests.post('https://simulatordata.onrender.com/data', data=json_data, headers={'Content-Type': 'application/json'})
    print(req.status_code)

def get_name_from_value(cls, value):
    for name, val in vars(cls).items():
        if not name.startswith("__") and val == value:
            return name.replace("_", " ").title() if "_" in name else name.title()
    return None

def close_app():
    global running
    print("Fechando o aplicativo...")
    running = False
    root.quit()
    root.destroy()

def start_gui():
    global root
    root = tk.Tk()
    root.title("Iracing Data Collector")
    root.geometry("300x100")

    close_button = tk.Button(root, text="Fechar", command=close_app)
    close_button.pack(expand=True)

    root.protocol("WM_DELETE_WINDOW", close_app)
    root.mainloop()

if __name__ == '__main__':
    ir = irsdk.IRSDK()
    state = State()
    currentLap = 0
    topSpeed = 0
    fuelStart = 0
    stintInfo = {}
    initialized = False
    running = True

    gui_thread = threading.Thread(target=start_gui, daemon=True)
    gui_thread.start()

    try:
        loop()
    except KeyboardInterrupt:
        print("Exiting...")
