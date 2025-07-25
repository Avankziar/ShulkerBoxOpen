package me.avankziar.sbo.general.assistance;

import java.util.regex.Pattern;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;

public class ChatApiS
{
	private static final Pattern po = Pattern.compile("(?<!\\\\)(&#[a-fA-F0-9]{6})");
	private static final Pattern pt = Pattern.compile("(?<!\\\\)(&[a-fA-F0-9k-oK-OrR]{1})");
	
	public static MiniMessage all = MiniMessage.builder()
			 .tags(TagResolver.standard())
			 .build();
	
	/**
	 * Only for Spigot, NOT Paper
	 * @param s
	 * @return
	 */
	public static net.md_5.bungee.api.chat.BaseComponent[] tl(String s)
	{
		if(s == null)
		{
			return BungeeComponentSerializer.get().serialize(all.deserialize(""));
		} else if(po.matcher(s).find() || pt.matcher(s).find())
		{
			//Old Bukkit pattern
			return BungeeComponentSerializer.get().serialize(all.deserialize(oldBukkitFormat(s)));
		} else
		{
			//new kyori adventure pattern
			return BungeeComponentSerializer.get().serialize(all.deserialize(s));
		}
	}
	
	public static String oldBukkitFormatShort(String s)
	{
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '&' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+7 < s.length())
				{
					String rc = s.substring(i, i+7);
					b.append(getBukkitHexColorConvertKyoriAdventure(rc));
					i += 7;
				} else
				{
					b.append(getBukkitColorConvertKyoriAdventure(cc));
					i++;
				}
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
	
	public static String oldBukkitFormat(String s)
	{
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '&' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+7 < s.length())
				{
					String rc = s.substring(i, i+7);
					b.append(getBukkitHexColorConvertKyoriAdventure(rc));
					i += 7;
				} else
				{
					b.append(getBukkitColorConvertKyoriAdventure(cc));
					i++;
				}
			} else if(c == '~' && i+2 < s.length())
			{
				char ca = s.charAt(i+1);
				char cb = s.charAt(i+2);
				if(ca == '!' && cb == '~')
				{
					b.append("<newline>");
				} else
				{
					b.append(c);
				}
				i += 2;
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
	
	public static String oldBukkitFormatOnly(String s)
	{
		return oldBukkitFormat(s.replace("~!~", ""));
	}
	
	private static String getBukkitColorConvertKyoriAdventure(char c)
	{
		String r = "";
		switch(c)
		{
		default:
			break;
		case '0':
			r = "<black>";
			break;
		case '1':
			r = "<dark_blue>";
			break;
		case '2':
			r = "<dark_green>";
			break;
		case '3':
			r = "<dark_aqua>";
			break;
		case '4':
			r = "<dark_red>";
			break;
		case '5':
			r = "<dark_purple>";
			break;
		case '6':
			r = "<gold>";
			break;
		case '7':
			r = "<gray>";
			break;
		case '8':
			r = "<dark_gray>";
			break;
		case '9':
			r = "<blue>";
			break;
		case 'a':
			r = "<green>";
			break;
		case 'b':
			r = "<aqua>";
			break;
		case 'c':
			r = "<red>";
			break;
		case 'd':
			r = "<light_purple>";
			break;
		case 'e':
			r = "<yellow>";
			break;
		case 'f':
			r = "<white>";
			break;
		case 'k':
			r = "<obf>";
			break;
		case 'l':
			r = "<b>";
			break;
		case 'm':
			r = "<st>";
			break;
		case 'n':
			r = "<u>";
			break;
		case 'o':
			r = "<i>";
			break;
		case 'r':
			r = "<reset>";
			break;
		}
		return r;
	}
	
	private static String getBukkitHexColorConvertKyoriAdventure(String hexnumber)
	{
		if(hexnumber.contains("&#"))
		{
			return "<"+hexnumber.replace("&", "")+">";
		} else if(hexnumber.contains("#"))
		{
			return "<"+hexnumber+">";
		}
		return "<#"+hexnumber+">";
	}
	
	public static String hover(String s, String hoverType, String hover)
	{
		switch(hoverType)
		{
		default:
		case "SHOW_TEXT":
			return "<hover:show_text:'"+oldBukkitFormat(hover)+"'>"+oldBukkitFormat(s)+"</hover>";
		case "SHOW_ITEM":
			return "<hover:show_item:'"+oldBukkitFormat(hover)+"'>"+oldBukkitFormat(s)+"</hover>";
		}
	}
	
	public static String hover(String s, String itemjson)
	{
		return "<hover:show_item:"+oldBukkitFormat(itemjson)+">"+oldBukkitFormat(s)+"</hover>";
	}
	
	public static String click(String s, String clickType, String click)
	{
		switch(clickType)
		{
		default:
		case "SUGGEST_COMMAND":
			return "<click:suggest_command:'"+click+"'>"+oldBukkitFormat(s)+"</click>";
		case "RUN_COMMAND":
			return "<click:run_command:'"+click+"'>"+oldBukkitFormat(s)+"</click>";
		case "OPEN_URL":
			return "<click:open_url:'"+click+"'>"+oldBukkitFormat(s)+"</click>";
		case "COPY_TO_CLIPBOARD":
			return "<click:copy_to_clipboard:'"+click+"'>"+oldBukkitFormat(s)+"</click>";
		}
	}
	
	public static String clickHover(String s, String clickType, String click, String hoverType, String hover)
	{
		StringBuilder sb = new StringBuilder();
		switch(hoverType)
		{
		default:
		case "SHOW_TEXT":
			sb.append("<hover:show_text:'"+oldBukkitFormat(hover)+"'>"); break;
		case "SHOW_ITEM":
			sb.append("<hover:show_item:"+oldBukkitFormat(hover)+">"); break;
		}
		switch(clickType)
		{
		default:
		case "SUGGEST_COMMAND":
			sb.append("<click:suggest_command:'"+click+"'>"+oldBukkitFormat(s)); break;
		case "RUN_COMMAND":
			sb.append("<click:run_command:'"+click+"'>"+oldBukkitFormat(s)); break;
		case "OPEN_URL":
			sb.append("<click:open_url:'"+click+"'>"+oldBukkitFormat(s)); break;
		case "COPY_TO_CLIPBOARD":
			sb.append("<click:copy_to_clipboard:'"+click+"'>"+oldBukkitFormat(s)); break;
		}
		sb.append("</click></hover>");
		return sb.toString();
	}
	
	public static String convertMiniMessageToOldFormat(String s)
	{
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '<' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+8 < s.length())
				{
					//Hexcolors
					//     i12345678
					//f.e. <#00FF00>
					String rc = s.substring(i, i+8);
					b.append(rc.replace("<#", "&#").replace(">", ""));
					i += 8;
				} else
				{
					//Normal Colors
					String r = null;
					StringBuilder sub = new StringBuilder();
					sub.append(c).append(cc);
					i++;
					for(int j = i+1; j < s.length(); j++)
					{
						i++;
						char jc = s.charAt(j);
						if(jc == '>')
						{
							sub.append(jc);
							switch(sub.toString())
							{
							case "</color>":
							case "</black>":
							case "</dark_blue>":
							case "</dark_green>":
							case "</dark_aqua>":
							case "</dark_red>":
							case "</dark_purple>":
							case "</gold>":
							case "</gray>":
							case "</dark_gray>":
							case "</blue>":
							case "</green>":
							case "</aqua>":
							case "</red>":
							case "</light_purple>":
							case "</yellow>":
							case "</white>":
							case "</obf>":
							case "</obfuscated>":
							case "</b>":
							case "</bold>":
							case "</st>":
							case "</strikethrough>":
							case "</u>":
							case "</underlined>":
							case "</i>":
							case "</em>":
							case "</italic>":
								r = "";
								break;
							case "<black>":
								r = "&0";
								break;
							case "<dark_blue>":
								r = "&1";
								break;
							case "<dark_green>":
								r = "&2";
								break;
							case "<dark_aqua>":
								r = "&3";
								break;
							case "<dark_red>":
								r = "&4";
								break;
							case "<dark_purple>":
								r = "&5";
								break;
							case "<gold>":
								r = "&6";
								break;
							case "<gray>":
								r = "&7";
								break;
							case "<dark_gray>":
								r = "&8";
								break;
							case "<blue>":
								r = "&9";
								break;
							case "<green>":
								r = "&a";
								break;
							case "<aqua>":
								r = "&b";
								break;
							case "<red>":
								r = "&c";
								break;
							case "<light_purple>":
								r = "&d";
								break;
							case "<yellow>":
								r = "&e";
								break;
							case "<white>":
								r = "&f";
								break;
							case "<obf>":
							case "<obfuscated>":
								r = "&k";
								break;
							case "<b>":
							case "<bold>":
								r = "&l";
								break;
							case "<st>":
							case "<strikethrough>":
								r = "&m";
								break;
							case "<u>":
							case "<underlined>":
								r = "&n";
								break;
							case "<i>":
							case "<em>":
							case "<italic>":
								r = "&o";
								break;
							case "<reset>":
								r = "&r";
								break;
							case "<newline>":
								r = "~!~";
								break;
							}
							b.append(r);
							break;
						} else
						{
							//Search for the color.
							sub.append(jc);
						}
					}
				}
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
}