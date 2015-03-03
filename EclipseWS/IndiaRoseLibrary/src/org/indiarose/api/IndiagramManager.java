package org.indiarose.api;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.lib.*;
import org.indiarose.lib.Bootstrap.BootstrapDelegate;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import android.content.Context;

/**
 * Provides an API To manage and use Indiagrams on any Application
 * The instanciation is asynchronous, and call IndiagramManagerDelegate.onIndiagramManagerInitialised() when finished
 * @author florentchampigny
 *
 */
public class IndiagramManager implements BootstrapDelegate {

	public interface IndiagramManagerDelegate {
		public void onIndiagramManagerInitialised();
	}

	public static IndiagramManager indiagramManager = null; //The singleton

	/**
	 * Return the singleton IndiagramManager
	 * @param context
	 * @param delegate
	 * @return
	 */
	public static IndiagramManager getInstance(Context context,
			IndiagramManagerDelegate delegate) {
		if (indiagramManager == null) {
			indiagramManager = new IndiagramManager(context, delegate);
		}
		return indiagramManager;
	}

	private IndiagramManagerDelegate delegate;

	private IndiagramManager(Context context, IndiagramManagerDelegate delegate) {
		this.delegate = delegate;
		try {
			Bootstrap.Initialize(context, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the list of all roots categories
	 * @return the list of all roots categories
	 */
	public List<Category> getRootCategories() {
		Category homeCategory = AppData.homeCategory;
		List<Category> categories = new ArrayList<Category>();

		for (Indiagram indiagram : homeCategory.indiagrams)
			if (indiagram instanceof Category)
				categories.add((Category) indiagram);

		return categories;
	}

	/**
	 * Return the list of all roots indiagrams/categories as Indiagrams
	 * @return the list of all roots indiagrams/categories as Indiagrams
	 */
	public List<Indiagram> getRootCategoriesaAsIndiagrams() {
		List<Indiagram> indiagrams = new ArrayList<Indiagram>();

		for (Indiagram indiagram : getRootCategories())
			indiagrams.add((Indiagram) indiagram);

		return indiagrams;
	}

	/**
	 * Return the list of all indiagrams of given category
	 * @return the list of all indiagrams of given category
	 */
	public List<Indiagram> getIndiagramsOfCategory(Category category) {
		List<Indiagram> indiagrams = new ArrayList<Indiagram>();

		for (Indiagram indiagram : category.indiagrams)
			indiagrams.add(indiagram);

		return indiagrams;
	}

	/**
	 * All ways to find a Category
	 * @author florentchampigny
	 *
	 */
	public enum CategoryBy {
		TEXT, PATH, STARTPATH;
	}

	/**
	 * Find a category by "CategoryBy"
	 * @param string Searched tet
	 * @param by CategoryBy : All ways to find a Category
	 * @return the category, or null
	 */
	public Category getCategoryBy(String string, CategoryBy by) {

		Category homeCategory = AppData.homeCategory;
		Category c = null;

		for (Indiagram indiagram : homeCategory.indiagrams)
			if (indiagram instanceof Category) {
				Category category = (Category) indiagram;
				if ((by.equals(CategoryBy.TEXT) && category.text.equals(string))
						|| (by.equals(CategoryBy.PATH) && category.filePath
								.equals(string))
								|| (by.equals(CategoryBy.STARTPATH) && category.filePath
										.startsWith(string))) {

					category = c;
					break;
				}
			}
		return c;
	}

	public Category getCategoryByText(String string) {
		return getCategoryBy(string, CategoryBy.TEXT);
	}

	public Category getCategoryByPath(String string) {
		return getCategoryBy(string, CategoryBy.PATH);
	}

	/**
	 * All ways to find an Indiagram
	 * @author florentchampigny
	 *
	 */
	public enum IndiagramBy {
		TEXT, PATH, STARTPATH;
	}

	/**
	 * Search an Indiagram in a category, by IndiagramBy 
	 * @param category the category to search in
	 * @param text the search text
	 * @param by see IndiagramBy
	 * @return the Indiagram, or null
	 */
	public Indiagram getIndiagramBy(Category category, String text,
			IndiagramBy by) {

		Indiagram i = null;

		for (Indiagram indiagram : category.indiagrams) {
			if ((by.equals(IndiagramBy.TEXT) && indiagram.text.equals(text))
					|| (by.equals(IndiagramBy.PATH) && indiagram.filePath
							.equals(text))) {
				i = indiagram;
				break;
			}
		}

		return i;
	}

	/**
	 * Get an Indiagram by it's unique Path
	 * @param path the Indiagram's identifier
	 * @return the indiagram, or null
	 */
	public Indiagram getIndiagramByPath(String path) {
		Category homeCategory = AppData.homeCategory;
		Indiagram i = null;

		System.out.println("trouver " + path);

		//RECHERCHE RAPIDE
		for (Indiagram iCategory : homeCategory.indiagrams)
			if (iCategory instanceof Category) {
				Category category = (Category) iCategory;

				String prefix = category.text;
				System.out.println(prefix);

				if (path.startsWith(prefix)) {
					Indiagram indiagram = getIndiagramBy(category, path,
							IndiagramBy.PATH);
					if (indiagram != null)
						return indiagram;
				}
			}
		//SI ON N A PAS TROUVE, ON LANCE UNE RECHERCHE COMPLEXE
		i = recherche(path);
		return i;
	}

	/**
	 * Recherche complexe/lente d'un indiagram
	 */
	private Indiagram recherche(String path){
		return recherche(AppData.homeCategory,path);
	}

	private Indiagram recherche(Category c , String path){
		for(Indiagram ic : c.indiagrams){
			if (ic instanceof Category) {
				Category category = (Category) ic;
				Indiagram i = recherche(category,path);
				if(i != null)
					return i;
			}else{
				if(ic.filePath.equals(path)){
					return ic;
				}
			}
		}
		return null;
	}

	@Override
	public void onBootstrapInitialised() {
		if (delegate != null)
			delegate.onIndiagramManagerInitialised();
	}
}
