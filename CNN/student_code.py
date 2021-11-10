# python imports
import os
from tqdm import tqdm

# torch imports
import torch
import torch.nn as nn
import torch.optim as optim

# helper functions for computer vision
import torchvision
import torchvision.transforms as transforms


class LeNet(nn.Module):

    def __init__(self, input_shape=(32, 32), num_classes=100):
        super(LeNet, self).__init__()
        # certain definitions
        self.input_shape = input_shape
        
        self.conv1 = nn.Conv2d(input_channels=3, output_channels=6, kernel_size=5, stride=1, padding=0)
        self.relu1 = nn.ReLU()
        self.pool1 = nn.MaxPool2d(kernel_size=2, padding=0, stride=2)
        
        self.conv2 = nn.Conv2d(input_channels=6, output_channels=16, kernel_size=5, stride=1, padding=0)
        self.relu2 = nn.ReLU()
        self.pool2 = nn.MaxPool2d(kernel_size=2, padding=0, stride=2)
        
        self.flatten = nn.Flatten(start_dim=3, end_dim=1)
        
        self.linear1 = nn.Linear(in_features=(input_shape[0] * input_shape[1]), out_features=256)
        self.relu3 = nn.ReLU()
        
        self.linear2 = nn.Linear(in_features=256, out_features=128)
        self.relu4 = nn.ReLU()
        
        self.output = nn.Linear(in_features=128, out_features=num_classes)
        
    def forward(self, x):
        N = x[0]
        C = x[1]
        W = x[2]
        H = x[3]
        shape_dict = {1:[N, C, W, H], 2:[N, C, W, H], 3:[N, C, W, H], 4:[N, C, W, H], 5:[N, C, W, H], 6:[N, C]}
        # certain operations
        return self, shape_dict


def count_model_params():
    '''
    return the number of trainable parameters of LeNet.
    '''
    model = LeNet()
    model_params = list(model.named_parameters()).__len__()

    return model_params


def train_model(model, train_loader, optimizer, criterion, epoch):
    """
    model (torch.nn.module): The model created to train
    train_loader (pytorch data loader): Training data loader
    optimizer (optimizer.*): A instance of some sort of optimizer, usually SGD
    criterion (nn.CrossEntropyLoss) : Loss function used to train the network
    epoch (int): Current epoch number
    """
    model.train()
    train_loss = 0.0
    for input, target in tqdm(train_loader, total=len(train_loader)):
        ###################################
        # fill in the standard training loop of forward pass,
        # backward pass, loss computation and optimizer step
        ###################################

        # 1) zero the parameter gradients
        optimizer.zero_grad()
        # 2) forward + backward + optimize
        output, _ = model(input)
        loss = criterion(output, target)
        loss.backward()
        optimizer.step()

        # Update the train_loss variable
        # .item() detaches the node from the computational graph
        # Uncomment the below line after you fill block 1 and 2
        train_loss += loss.item()

    train_loss /= len(train_loader)
    print('[Training set] Epoch: {:d}, Average loss: {:.4f}'.format(epoch + 1, train_loss))

    return train_loss


def test_model(model, test_loader, epoch):
    model.eval()
    correct = 0
    with torch.no_grad():
        for input, target in test_loader:
            output, _ = model(input)
            pred = output.max(1, keepdim=True)[1]
            correct += pred.eq(target.view_as(pred)).sum().item()

    test_acc = correct / len(test_loader.dataset)
    print('[Test set] Epoch: {:d}, Accuracy: {:.2f}%\n'.format(
        epoch + 1, 100. * test_acc))

    return test_acc
