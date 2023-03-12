# Majority Graph

## Description

This program simulates opinion changes in group of agents. To simulate agents impact on their neighbors, majority model was implemented. It considers triads that form inside the group and overall opinion that is being set in each of them after agents connect with each other. This program allows inspecting agents group by setting the initial number of positive (for) opinions, number of Zealots (agents with static opinions) and their opinions, external opinion fields, that can influence agents on changing their opinions and chance of settling uniform opinion in a triad.

It visualizes group in which opinion is being spread as coloured bobbles where each bobble represents one agent (group member). To better illustrate overall opinion distribution over time chart panel was introduced. Chart was created with help of JFreeChart external library.

<!-- Image -->
![Fluent mode](https://github.com/BartiWhite/Majority-graph/blob/main/images/fluent.png)

Algorithm progress can be followed closely in step-by-step mode that show opinion establishment in each consecutive triad.

<!-- Image -->
![Step-by-step mode](https://github.com/BartiWhite/Majority-graph/blob/main/images/step-by-step1.png)
![Step-by-step mode](https://github.com/BartiWhite/Majority-graph/blob/main/images/step-by-step2.png)

## Usage

To set up project on your machine first clone above repository:

> git clone https://github.com/BartiWhite/Majority-graph.git

Now you can open cloned project in your IDE and run it.

Comprehensive description of application usage was implemented within the app itself.

## Contribution

For any contribution please create new branch for your changes and submit Pull Request with description.
