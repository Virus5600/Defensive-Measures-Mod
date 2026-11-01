<div align="center" style="text-align: center">
	<img src="https://img.shields.io/github/actions/workflow/status/Virus5600/Defensive-Measures-Mod/build.yml?logo=gradle&amp;label=Build%20Check" alt="GitHub Actions Workflow Status">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/github/actions/workflow/status/Virus5600/Defensive-Measures-Mod/fabric.yml?logo=gradle&amp;label=Fabric%20Test%20Runner" alt="GitHub Actions Workflow Status">&nbsp;&nbsp;&nbsp;&nbsp;
</div>
<br>

<div align="center" style="text-align: center">
	<img src="https://img.shields.io/modrinth/v/defensive-measures?logo=modrinth&amp;label=Latest%20Modrinth%20Release" alt="Modrinth Version">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/curseforge/v/700629?logo=curseforge&amp;label=Latest%20CurseForge%20Release" alt="CurseForge Version">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/github/v/tag/Virus5600/Defensive-Measures-Mod?include_prereleases&amp;sort=semver&amp;logo=github&amp;label=Latest%20GitHub%20Tag" alt="GitHub Tag">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/github/v/release/Virus5600/Defensive-Measures-Mod?logo=semanticrelease&amp;label=Latest%20Release" alt="GitHub release (latest by date)">&nbsp;&nbsp;&nbsp;&nbsp;
</div>
<br>

<div align="center" style="text-align: center">
	<img src="https://img.shields.io/modrinth/dt/defensive-measures?logo=modrinth&amp;label=Modrinth%20Downloads" alt="Modrinth Downloads">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/curseforge/dt/700629?logo=curseforge&amp;label=CurseForge%20Downloads" alt="CurseForge Downloads">&nbsp;&nbsp;&nbsp;&nbsp;
	<img src="https://img.shields.io/github/downloads/Virus5600/Defensive-Measures-Mod/total?logo=github&amp;label=GitHub%20Downloads" alt="GitHub Downloads (all assets, all releases)">&nbsp;&nbsp;&nbsp;&nbsp;
</div>
<br>

<div align="center" style="text-align: center">
	<img src="https://cf.way2muchnoise.eu/versions/700629.svg" alt="CurseForge Versions">&nbsp;&nbsp;&nbsp;&nbsp;
</div>

<br>
<div align="center" style="text-align: center">
	<img src="https://raw.githubusercontent.com/Virus5600/Defensive-Measures-Mod/refs/heads/main/Defensive%20Measures%20Add-On%20Banner.png" alt="Defensive Measures Banner" width="100%">
</div>
<br>

<div align="center" style="text-align: center">
	<a href="https://fabricmc.net/">
		<img
			src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_64h.png"
			alt="Available on Fabric"
			width="200">
	</a>
	<a href="https://quiltmc.org/">
		<img
			src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/quilt_64h.png"
			alt="Available on Quilt"
			width="200">
	</a>
</div>

<hr>

# Defensive Measures

Defensive Measures is a mod that allows players to place defensive turrets and traps to protect
themselves, their creations, positions, and so on. The mod adds <b><i>Towers/Turrets</i></b>,
<b><i>Traps</i></b>, <b><i>Entities</i></b>, <b><i>Structures</i></b>, <b><i>Villager Variants</i></b>,
and <b><i>Items</i></b> to the game, which opens a lot of new capabilities, ideas, and creative
imaginations for the players to explore.

## AI Disclosure/Transparency

This mod is not AI-generated. All content, including code, assets, and documentation, is created by the
mod author and contributors. The mod development process involves human creativity, design, and
programming skills.

While AI tools may be used for certain tasks, such as code formatting, code rechecking, idea generation,
moral support, Fabric and Minecraft documentation lookup, and other non-creative tasks, the core development
and content creation are done by humans. Listed below are all the tasks that AI tools are used for in this mod,
along with what AI tools are used for each task:

### Code Formatting
Built-in IDE feature (IntelliJ) for formatting and auto-completion. As the author/developer is unsure whether
the IDE is using AI for the auto-completion and code suggestion, it will be added to the list of AI usage to
be transparent.

### Code Rechecking
Co-pilot for code rechecking and suggestions (in PRs). While Co-pilot is used to push some fixes on certain
parts, the author/developer always reviews and approves the changes before merging them into the main
codebase.

On some instances, Claude is also used to recheck code snippets to see if there's some better way to
implement or optimize the code. There would also be times when the code will be improved by Claude and/or
Gemini, but the author/developer will always review and approve the changes before merging them into the main
codebase by reading each and every line and understand what's happening and if it is better than the current
implementation and if the changes won't break anything or if said code is rigid.

Rigid code that is suggested by the AIs, if better than the current implementation, will be implemented
and abstracted manually by the author/developer to ensure that the code is not rigid and can be easily
modified in the future, and could be easily extended to add more features without breaking the current
implementation.

### Idea Generation
Ideas flow in naturally from the author/developer's mind. Theme checking, idea validation, and research
are done by the author/developer to ensure the ideas are feasible and align with the mod's vision. One such
example of a feature that's now implemented in the mod is the Anti-Tank Turret (Hawkins Grenade was raised
by Gemini as a landmine suggestion alongside the M14 Toepopper).

Originally, the landmine is just a singular instance and without AT or AP classifications. The suggestion
raised by Gemini allowed the author/developer to explore the idea of having a landmine that can be
classified as AT or AP, which led to the separation of the AT Landmine and AP Landmine, which also opened
the possibility for more AT and AP landmine variations than just a generalized landmine.

On some occasions, the author/developer would also have ideas and given that AIs are just statistical
models, they could end up validating an idea proposed by the author/developer even though it is a horrible
one or will have a bad execution. As such, the author/developer would go out of their way to ensure that
the idea is "good" and will work well by asking for feedback through external means. One such example is
an idea of implementing an advancement related to the Bloody 100th, which was, for the author/developer,
sounded good (due to the dev being sleep deprived after coding for 2 days straight) but was extremely
horrible as it instead sounds like glorifying the horrible side of how they got their title instead of
actually giving them the recognition the author/developer was initially thinking.

In detail, the advancement is supposed to be acquired by the player when the AA Turret (designed to look
like the German Flak 88) is placed and the player is able to shoot down 100 Phantoms (representing the
100th's ability to shoot down 100 aircraft). However, the advancement is instead scrapped in favor of a
suggestion made by someone in Reddit, focusing on the Battle of Great Britain, referencing ["London Can Take
It"](https://github.com/Virus5600/Defensive-Measures-Mod/issues/66) by shooting down 100 Phantoms with a
different variant of an AA Turret (modelled after an allied AA Turret).

### Moral Support
There are several instances wherein the author/developer was stuck on certain problems and/or issues and
needed someone to talk to. This comes from the AIs (ofc, there's also the friends and family but they
aren't available all the time); specifically Gemini and Claude. On some occasions, the author/developer
would also work on the mod for extended periods of time and would need someone to talk to, which is where
the AIs come in. The AIs are always available and can provide support and guidance when needed. Think of
this section as the author/developer using them like a chatbot talking about nonsense and random stuff.

### Fabric and Minecraft Documentation Lookup
While documentation is available online, there would be times when the author is "blind" and would miss
certain stuff (Yes, I missed the rendering section in the Fabric Wiki and Gemini insisted it existed. I was
wrong and Gemini was right). The AIs are used to look up the Fabric and Minecraft documentations to ensure
that the author/developer is not missing anything. Links to the sources is always asked to confirm the
information. Otherwise, the author/developer would have been stuck on a lot of stuff already.

### Grammar and Spelling

With English being the author's second language, there would be times when the author/developer would ask
the AIs to double check their grammar and spelling to ensure that the documentation is clear and
understandable. This README is one such example where the author/developer asked the AIs to check the
grammar and spelling to ensure clarity and understandability... and for correct spelling of various words
that still confuses the author despite using it several times already (i.e. "ocassions" instead of
"occasions", and so on, and also typos).

---

This list will be updated as the mod development progresses and more tasks are added or removed from this
AI usage list. The author/developer is committed to transparency and will provide updates on the AI usage
in the mod development process.

## SUPPORT ME

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/D1D6BFRR3)
<br><br>
<a href="https://www.paypal.me/satch5600"><img src="https://raw.githubusercontent.com/Virus5600/Defensive-Measures-Add-on-Guide/refs/heads/main/public/images/UI/paypal_donate.png" height="30px"></a>
