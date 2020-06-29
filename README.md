# Slug Invaders
A 2D Java game about a gardener who has had enough of invading slugs and is now resorting to specialized methods of slug eradication.

The game has one protagonist (you, the player, the gardener) that can move horizontally and shoot "salt bullets" onto slugs which will damage/kill them once hit. 
The slugs will come in waves, you, the gardener, will need to kill these slugs before they pick up your plants and carry them away to be eaten.
The slugs will become more resistive and faster as they adapt to your salt gun and other salt weaponry.
However if the slugs are successful in stealing all your plants, they will eventually decide that it is time to avenge all their slaughtered brothers, thus killing you and taking your body as their trophy of success.

This project/game is work in progress, and only worked on now and then, as a hobby. You can't ever know when the last commit will be pushed. Maybe it has, probably not.

Features so far:
- Very playable. But:
-- Wave logic and continuous generation is completed. However variety is on the todo list, and the generation probably needs some more tuning.
-- Moving and shooting is completed. However the shooting part, more specifically the ammo part, is up for change. I'm not such a fan of how it works right now.
-- Slug AI is 98% fully working. However this also needs tuning and some minor bug fixes.
-- Special weaponry/perks is being worked on. The only "perk" currently 100% working is the Salt Wave.
- Local highscores. (Todo: Encrypt/obscure the data or something to make manual editing of the data more difficult, if not impossible)
- Supports a lot of different gamepads using Jamepad(https://github.com/williamahartman/Jamepad) as a dependency. Personally being tested with a Xbox One controller on Windows 10.
- Needs work on navigation in menus though. But 100% supported once you are in-game.

Major Todo List (Will always be stuff added/changed/improved outside of this list aswell):
- More slug types and variation in art, and possibly behaviour aswell. Definitely different attributes for each new type.
- More handheld weaponry, possibly. Unsure yet, need to work out the ammo part mentioned earlier first. In the case this gets added, I might consider customizable character aswell, in terms of looks.
- Resizable game. Currently just HD (720px) static resolution. Ouch.
- Skillfully attainable perks and randomly spawned perks. (Weaponry aswell)
- Might add a variation of maps, possibly customizable maps aswell. Not sure.
- A lot else I can't think of as im writing this.

Originally created by me (Magnus) and my friend (Jarand) as a quick school project back in 2017. 
All code is programmed by me (Magnus/mrmag518) except for lib(s) that are obviously not, such as Tinysound and Imgscalr.
My friend had a big role in designing the original basis of the game, such as art and ideas for features and of course the original idea of what the game should be about.
