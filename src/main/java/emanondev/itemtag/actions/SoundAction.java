package emanondev.itemtag.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.itemedit.Util;

public class SoundAction extends Action {

	public SoundAction() {
		super("sound");
	}

	@Override
	public void validateInfo(String text) {
		if (text.isEmpty())
			throw new IllegalStateException();
		String[] args = text.split(" ");
		if (args.length > 4)
			throw new IllegalStateException();
		if (Sound.valueOf(args[0].toUpperCase())==null)
			throw new IllegalStateException();
		if (args.length >= 2) {
			if (Float.valueOf(args[1]) <= 0)
				throw new IllegalStateException();
		}
		if (args.length >= 3) {
			if (Float.valueOf(args[2]) <= 0)
				throw new IllegalStateException();
		}
		if (args.length >= 4) {
			switch (args[3].toLowerCase()) {
			case "true":
			case "false":
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public void execute(Player player, String text) {
		String[] args = text.split(" ");
		boolean self = false;
		float volume = 1;
		float pitch = 1;
		Sound sound = Sound.valueOf(args[0].toUpperCase());
		if (args.length >= 2)
			volume = Float.valueOf(args[1]);
		if (args.length >= 3)
			pitch = Float.valueOf(args[2]);
		if (args.length >= 4)
			self = Boolean.valueOf(args[3]);
		if (self)
			player.playSound(player.getLocation(), sound, volume, pitch);
		else
			player.getLocation().getWorld().playSound(player.getLocation(), sound, volume, pitch);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, List<String> params) {
		switch (params.size()) {
		case 1:
			return Util.complete(params.get(0), Sound.class);
		case 2:
			return Util.complete(params.get(1), Arrays.asList("0.5", "1"));
		case 3:
			return Util.complete(params.get(2), Arrays.asList("1", "2", "5", "10"));
		case 4:
			return Util.complete(params.get(3), Arrays.asList("true", "false"));
		}
		return Collections.emptyList();
	}

	@Override
	public List<String> getInfo() {
		ArrayList<String> list = new ArrayList<>();
		list.add("&b"+getID()+" &e<sound> [volume] [pitch] [self]");
		list.add("&e<sound> &bthe sound to play");
		list.add("&e[volume] &bthe volume of the sound, default 1");
		list.add("&e[pitch] &bthe pitch of the sound, default 1");
		list.add("&e[self] &bonly player sould heard?, by default false");
		return list;
	}
}
