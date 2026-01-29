# Reflection

## How would you evaluate your work? What went well? What would you do differently?
I would say I implemented most of the scenarios in the feature doc. I’m pretty happy with the logic in the frontend and I think it does a good job handling most of the scenarios. In terms of handling the full stack application and cementing the front end from user error or user caused breaks, I would say that also went well. Also worth noting that I added logic to make it so the frontend will “optimistically” show immediate changes to the UI from settings changes, but will revert if it receives an error from the backend when saving the changes to the backend. This will keep the frontend honest in terms of what the actual configurations are.

In a real development scenario, I would build out the frontend more in terms of the aesthetics, but I wanted to spend more time on the logic and ensuring the requirements were met. I would also use an actual database, since the retained data in the application is in memory in the backend.

## What was an insight you gained or something you learned while working on this?
Ironically, I’ve never thought much about how traffic lights work, so just thinking through the logic there and making sure it makes sense and the user wouldn’t be able to mess up the synchronization took more time to think through than I thought it would. Assigning certain timing ratios to the different colors so the coordination aligned regardless of speed settings made me think through a lot of different cases to handle and fix which is evident in the functions App.vue.

## If you were to add another test challenge to this, what would it be? Why?
I was considering adding a left turn in the mix which would further complicate the light timing logic. I think this would be fairly hard to do, especially if already struggling with the basic synchronization. It could be an extra challenge.

