import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from torchvision import datasets, transforms

# Feel free to import other packages, if needed.
# As long as they are supported by CSL machines.


def get_data_loader(training=True):
    """
    TODO: implement this function.

    INPUT: 
        An optional boolean argument (default value is True for training dataset)

    RETURNS:
        Dataloader for the training set (if training = True) or the test set (if training = False)
    """
    custom_transform = transforms.Compose([
        transforms.ToTensor(),
        transforms.Normalize((0.1307,), (0.3081,))
        ])
    
    train_set = datasets.MNIST('./data', train=True, download=False, transform=custom_transform)
    test_set = datasets.MNIST('./data', train=False, transform=custom_transform)
    
    if training:
        loader = torch.utils.data.DataLoader(train_set, batch_size=50)  # @UndefinedVariable
    else:
        loader = torch.utils.data.DataLoader(test_set, batch_size=50)  # @UndefinedVariable
    
    return loader


def build_model():
    """
    TODO: implement this function.

    INPUT: 
        None

    RETURNS:
        An untrained neural network model
    """
    model = nn.Sequential(
                nn.Flatten(),
                nn.Linear(784, 128),
                nn.ReLU(),
                nn.Linear(128, 64),
                nn.ReLU(),
                nn.Linear(64, 10))
    return model


def train_model(model, train_loader, criterion, T):
    """
    TODO: implement this function.

    INPUT: 
        model - the model produced by the previous function
        train_loader  - the train DataLoader produced by the first function
        criterion   - cross-entropy 
        T - number of epochs for training

    RETURNS:
        None
    """
    opt = optim.SGD(model.parameters(), lr=0.001, momentum=0.9)
    model.train()
    for epoch in range(T):
        running_loss = 0.0
        num_correct = 0
        num_samples = 0
        for batch in train_loader:
            inputs, labels = batch
            opt.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            opt.step()
            running_loss += loss.item()
            # TODO figure out what accuracy means and how to print it
            scores = model(inputs)
            _, predictions = scores.max(1)
            num_correct += (predictions == labels).sum()
            num_samples += predictions.size(0)
            accuracy_as_percent = float((num_correct / num_samples) * 100)
        print("Train Epoch: {}  Accuracy: {}/{}({}%)  Loss: {}".format(epoch, num_correct, num_samples, round(accuracy_as_percent, 2), round(running_loss / num_samples, 3)))


def evaluate_model(model, test_loader, criterion, show_loss=True):
    """
    TODO: implement this function.

    INPUT: 
        model - the the trained model produced by the previous function
        test_loader    - the test DataLoader
        criterion   - cropy-entropy 

    RETURNS:
        None
    """ 

    model.eval()
    total = 0
    correct = 0
    total_loss = 0
    with torch.no_grad():
        for data, labels in test_loader:
            outputs = model(data)
            loss = criterion(outputs, labels)            
            total_loss += loss.item()
            _, predicted = outputs.data.max(1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()
    accuracy_as_percent = float((correct / total) * 100)
    if not show_loss:
        print("Accuracy: {}%".format(round(accuracy_as_percent, 2)))
    else:
        print("Average loss: {}".format(round(total_loss / total, 4)))
        print("Accuracy: {}%".format(round(accuracy_as_percent, 2)))

        
def predict_label(model, test_images, index):
    """
    TODO: implement this function.

    INPUT: 
        model - the trained model
        test_images   -  test image set of shape Nx1x28x28
        index   -  specific index  i of the image to be tested: 0 <= i <= N - 1


    RETURNS:
        None
    """
    logits = model(test_images)
    prob = F.softmax(logits, dim=1)
    tensors = list(prob[index])
    probabilities = []
    for tensor in tensors:
        probabilities.append(tensor.item() * 100)
    class_names = ['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine']
    for _ in range(3):
        maxval = probabilities[0]
        index = 0
        for j in range(1, probabilities.__len__()):
            if probabilities[j] > maxval:
                maxval = probabilities[j]
                index = j
        print("{}: {}%".format(class_names[index], round(probabilities[index], 2)))
        probabilities.remove(maxval)
        class_names.remove(class_names[index])


if __name__ == '__main__':
    '''
    Feel free to write your own test code here to exaime the correctness of your functions. 
    Note that this part will not be graded.
    '''
    train_loader = get_data_loader()
    test_loader = get_data_loader(False)
    model = build_model()  # hope this is right
    criterion = nn.CrossEntropyLoss()
    train_model(model, train_loader, criterion, T=5)
    evaluate_model(model, test_loader, criterion, True)
    
    test_images = []
    for dat in test_loader:
        imgs, labels = dat
        test_images.append(imgs)
    test_tensor = torch.cat(test_images, dim=0)  # @UndefinedVariable
    
    predict_label(model, test_tensor, 0)
    
