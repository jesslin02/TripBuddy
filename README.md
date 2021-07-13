Original App Design Project - README Template
===

# Travel Planner

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Input locations, dates, events, hotels, and transportation to help plan your trip. In the app, you'll be able to see your itinerary as a schedule and on a map.

### App Evaluation
- **Category:** Travel
- **Mobile:** uses maps and real-time location to let user know estimated travel time between activities. can also include push notifications as reminders for upcoming events.
- **Story:** allows users to view their itinerary all in one place.
- **Market:** anyone who is planning to travel can enjoy this app. ability to add different types of events, transportation, and hotels.
- **Habit:** users can use this app any time they are going on a trip. 
- **Scope:** start out as a simple design with itinerary, with each item having a location, start/end date, notes, link, etc. can expand to include friends (group trips) or location-based suggestions.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* user can create a new trip
* user can add activities
    * start/end time & date
    * location
    * phone
    * website
    * notes
* user can view itinerary (list view)
* user can view map
* user can delete activities
* user can create a new account
* user can login/logout

**Optional Nice-to-have Stories**

* additional activity functions
    * detail view
    * different types: transportation, hotel, restaurant, tour, event
    * edit activity
    * see notifications for upcoming activities
    * attach photos 
    * attach pdf documents
    * export as txt
* user can duplicate activities
* user can add friends to trip
* user can see suggestions for things to do nearby

### 2. Screen Archetypes

* login screen
   * user can login
* registration screen
   * user can create a new account
* home stream
    * user can view list of trips
* trip creation
    * user can create a new trip with title and dates
* trip detail
    * user can see overview of a single trip
    * includes buttons to navigate to:
        * itinerary
        * map
        * different types of events
        * event creation screen
* event creation
    * user can add a new event with start/end date & time, location, website, notes
    * event creation choice (stretch ?)
        * user chooses which type of event they want to create
        * app will ask for different details in event creation depending on type of event
            * for hotels, will ask for check-in / check-out
            * for flights, will ask for flight number, departure/arrival locations & times
* itinerary stream
    * user can view a schedule of events in a list view
* map view
    * user can visualize trip on a map
* event detail
    * user can view start/end date & time, location, website, notes of a single event


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* home stream
* alerts (?)
* profile

**Flow Navigation** (Screen to Screen)

* login screen
  --> home stream
* registration screen
  --> home stream
* home stream
  --> trip creation
  --> trip detail
* trip creation
  --> trip detail
* trip detail
  --> activity creation
  --> itinerary stream
  --> map view
* event creation
  --> multiple screens for creation process (event choice, location?)
  --> trip stream
* itinerary stream
  --> event detail
* map view
  --> event detail
* event detail
  --> none 

## Wireframes

<img src="https://i.imgur.com/aOcfgO0.png" width=650>

[//]: <> (### [BONUS] Digital Wireframes & Mockups)

[//]: <> (### [BONUS] Interactive Prototype)

## Schema 
### Models
#### User

| Property | Type | Description               |
| -------- | ---- | ------------------------- |
| Name     |String| user's display name       |
| Username |String| unique username for login |
| Password |String| strong password for login |

#### Trip

| Property   | Type            | Description               |
| ---------- | --------------- | ------------------------- |
| User       | Pointer to User | user who created the trip |
| Name       | String          | title of trip             |
| Start date | Date            | start date of trip        |
| End date   | Date            | end date of trip          |
| Destination| String          | city where trip is located|
| Description| String          | short notes about trip    |

#### Event

| Property   | Type            | Description                |
| ---------- | --------------- | -------------------------- |
| Trip       | Pointer to Trip | trip that event is part of |
| User       | Pointer to User | user who created the trip  |
| Name       | String          | title of event             |
| Start date | Date            | start date of event        |
| End date   | Date            | end date of event          |
| Location   | String          | where event is located     |
| Phone      | long            | phone number of venue      |
| Website    | String          | website of venue           |
| Notes      | String          | short notes about event    |


### Networking
#### Home Stream Screen
* (Read/GET) query all trips where the current user is creator
* (Update/PUT) update trip details
* (Delete) delete existing trip
#### Trip Creation Screen
* (Create/POST) create a new trip object
#### Itinerary Screen
* (Read/GET) query all events that are part of the current trip
* (Update/PUT) update event details
* (Delete) delete existing event
#### Event Creation Screen
* (Create/POST) create a new event object
#### Profile Screen
* (Read/GET) query logged in user object

[//]: <> (- [Create basic snippets for each Parse network request])
[//]: <> (- [OPTIONAL: List endpoints if using existing API such as Yelp])
