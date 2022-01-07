import sys
import getopt
import torch
import torchvision
import torch.nn as nn
import torchvision.transforms as transforms
import torchvision.datasets as dsets
from torchvision.datasets import MNIST
import matplotlib.pylab as plt
import numpy as np
from torch.optim import Adam, SGD
import cv2
import os

class CNN(nn.Module):
  def __init__(self):
    super(CNN,self).__init__()
    self.conv1 = nn.Sequential(nn.Conv2d(in_channels=1, out_channels=16, kernel_size=5, stride=1, padding=2),nn.ReLU(),nn.MaxPool2d(kernel_size=2))
    self.conv2 = nn.Sequential(nn.Conv2d(in_channels=16, out_channels=32, kernel_size=5, stride=1, padding=2), nn.ReLU(), nn.MaxPool2d(kernel_size=2))
    self.fc = nn.Linear(2048,10)
  def forward(self, sample):
    out = self.conv1(sample)
    out = self.conv2(out)
    out = out.view(out.size(0), -1)
    out = self.fc(out)
    return out  

def openImage(f):
    img = cv2.imread(os.getcwd()+"/images/"+f)
    img = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    img = torchvision.transforms.functional.to_tensor(img.astype(np.uint8).reshape((32,32)))
    return img.reshape([1,1,32,32])

def predict(model,ip):
    _,yhat = torch.max(model(ip).data,1)
    return yhat.item

def printResults(res):
    for r in res:
        print(r)

def main(files):
    device = torch.device("cpu")
    model = CNN()
    model.to(device)
    model.load_state_dict(torch.load(os.getcwd()+"/model/MNIST_Model.model",map_location = 'cpu'))
    results=[]
    files = files.split(",")
    files = ["img0.png"]
    for f in files:
        img = openImage(f)
        img.to(device)
        results.append(predict(model,img))
    printResults(results)



if __name__ == "__main__":
    main(sys.argv[1])
