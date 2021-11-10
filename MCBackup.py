'''
Created on Jan 15, 2020
Last Edited on Jan 16, 2020
@author: Noah Zurn
# version 1.0
# First attempt at a python program
# idea is to backup minecraft world
# to another place on disk, possible internet backup implementation
# in the future
'''
import shutil
import os
from datetime import datetime
from distutils.dir_util import copy_tree
from operator import itemgetter
         
def backup():
    destPath = "C:/Users/Noah/MinecraftBackups/"
    os.chdir(destPath)
    now = datetime.now()
    now = now.strftime("%b-%d-%Y, %I-%M-%S %p")
    os.makedirs(now)
    destPath = destPath + now
    os.chdir(destPath)
    srcPath = "C:/Users/Noah/AppData/Roaming/.minecraft/saves/Sav and Noah 2 Electric Boogaloo"
    copy_tree(srcPath, destPath)
    maxSaves = 10
    destPath = "C:/Users/Noah/MinecraftBackups/"
    os.chdir(destPath)
    sortedFiles = sortFilesByDate(destPath)
    purgeOldFiles(sortedFiles, maxSaves)
    
def sortFilesByDate(directory):
    fileList = os.listdir(directory)
    fileDates = {}
    for file in fileList:
        fileDates[file] = os.stat(os.path.join(directory, file)).st_ctime
    fileDates = sorted(fileDates.items(), key = itemgetter(1))
    #print(fileDates)
    return fileDates

def purgeOldFiles(sortedFiles, maxSaves):
        numFilesToDelete = len(sortedFiles) - maxSaves
        for i in range(0, numFilesToDelete):
            shutil.rmtree(sortedFiles[i][0]);
        
backup()