package emanondev.itemtag;

import org.bukkit.inventory.ItemStack;

public interface TagManager {

	public boolean hasBooleanTag(String key,ItemStack item);
	public boolean hasIntegerTag(String key,ItemStack item);
	public boolean hasDoubleTag(String key,ItemStack item);
	public boolean hasStringTag(String key,ItemStack item);
	
	public ItemStack removeTag(String key,ItemStack item);
	
	public ItemStack setTag(String key,ItemStack item,boolean value);
	public ItemStack setTag(String key,ItemStack item,String value);
	public ItemStack setTag(String key,ItemStack item,int value);
	public ItemStack setTag(String key,ItemStack item,double value);
	
	public Boolean getBoolean(String key,ItemStack item);
	public String getString(String key,ItemStack item);
	public Integer getInteger(String key,ItemStack item);
	public Double getDouble(String key,ItemStack item);

}
