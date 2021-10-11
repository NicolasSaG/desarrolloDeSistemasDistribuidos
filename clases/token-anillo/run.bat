@echo off


set nodos= 0 1 2

for %%a in (%nodos%) do (
    
    start cmd /k java Anillo %%a
)

