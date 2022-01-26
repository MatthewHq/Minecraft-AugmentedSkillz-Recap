# AugmentedSkillz: Minecraft Plugin

### Gameplay extension suite via introduction of skills made for parallel game "world" usage in "dynamic" and "static" worlds

### Features:
- ### General
  - Custom Items
  - Custom UI
  
  ![Menu](https://i.imgur.com/BOguNya.gif)
  
  ![Craft](https://i.imgur.com/ukaACIt.gif)
  - Toggled Skill Experience Tracking Board
  - Administrative setup and amanagement command suite 
  - Skill experience requirements modeled exponentially 
    - [Link to below spreadsheet](https://docs.google.com/spreadsheets/d/15sccwq58ixYbp73EeotfpHGVj1Y-4rtTbugURanK3iM/edit#gid=1884957078): [![video discussion](https://i.imgur.com/Pfwcnx5.png)](https://docs.google.com/spreadsheets/d/15sccwq58ixYbp73EeotfpHGVj1Y-4rtTbugURanK3iM/edit#gid=1884957078)
    - XP scaling![image](https://i.imgur.com/HOHBYYR.png)
    
- ### Skills
  - Crafting: Paralleled Extension to crafting of both regular items and AugmentedSkillz items
    - Crafting UI
    - Built-in recipe reagant list to avoid recipe learning curve
  -  Runecrafting: Creation of "rune" items via travel to nodes
      - Node registration commands
	    - Introduction of uniquely interactable items
  -  Cooking: Paralleled Extension to cooking of both regular items and AugmentedSkillz items
  -  Mining: Alternative to regular mining style, which is easily exploited/cheated
      - Static world node replenishment
	    - Config toggled ore stacking
  -  Farming: Paralleled Extension to farming of regular items
  -  Fishing: Paralleled Extension to fishing of both regular items and AugmentedSkillz items
- ### Technical
  - ### MySQL Support
    - Batch and Single updates to ensure performance and reliability
    - Automatic Database setup and management
  - ### Config File Support
    - Main Config File
      - "dat" - DRM Check URL snippet (Disabled Now)
      - Tagged Drop control (Unique Items from skill or regular)
      - Experience point customization
    - User Data Folder (if not opting in to MySQL)
    - Chunk Node File coordinates (for skill function marking / plugin performance
    - Queue binary file, serialized list of coordinates to be replenished
    - RCpairs binary file, serialized HashMap of Runecrafting Pairs
  - ### Unique Item Metadata System
    - Special "new" items with custom names, description data, and interactability 
    - Item Stack prevention based on custom item rules
    - Hidden Meta Data UUID per item, useful for future data mining / logging item flow activity

