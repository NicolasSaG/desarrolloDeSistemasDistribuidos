@echo off


set nodos= 0 1 2 3 4 5 6 7

for %%a in (%nodos%) do (
    
    start cmd /k java Servidor2 %%a
)


