package org.indiarose.lib.model;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.view.IndiagramView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Class to manage an indiagram entity.
 * 
 * IDENTIFIANT UNIQUE DE L OBJET : filePath
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 * 
 */
public class Indiagram {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	/**
	 * Text associated with the indiagram. This text will be displayed on the
	 * selection view and spoken if no sound has been provided.
	 */
	public String text = "";
	/**
	 * Path to the image file.
	 */
	public String imagePath = "";
	/**
	 * Path to the sound file.
	 */
	public String soundPath = "";
	/**
	 * Path to the xml file.
	 */
	public String filePath = "";
	/**
	 * Indiagram view.
	 */
	protected IndiagramView m_view = null;

	/**
	 * Construct an instance with default value. Used for XmlBinding.
	 */
	public Indiagram() {

	}

	/**
	 * Construct an indiagram by specifying text, image and sound.
	 * 
	 * @param _text
	 *            : the text of the indiagram.
	 * @param _imagePath
	 *            : the path to the image associated with the indiagram.
	 * @param _soundPath
	 *            : the path to the sound associated with the indiagram.
	 */
	public Indiagram(String _text, String _imagePath, String _soundPath) {
		this.text = _text;
		this.imagePath = _imagePath;
		this.soundPath = _soundPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object _other) {
		if (!(_other instanceof Indiagram)) {
			return false;
		}

		Indiagram other = (Indiagram) _other;
		return (other.filePath.equalsIgnoreCase(this.filePath))
				&& (other.imagePath.equalsIgnoreCase(this.imagePath))
				&& (other.soundPath.equalsIgnoreCase(this.soundPath))
				&& (other.text.equalsIgnoreCase(this.text));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Indiagram clone() {
		Indiagram other = new Indiagram(this.text, this.imagePath,
				this.soundPath);
		other.filePath = this.filePath;
		return other;
	}

	/**
	 * Reset file relative data.
	 */
	public void resetFileData() {
		this.filePath = "";
	}

	/**
	 * Method to get the associated view.
	 * 
	 * @return the view of the current indiagram.
	 */
	public IndiagramView getView() {
		synchronized (this) {
			if (this.m_view == null
					|| !this.m_view.getContext().equals(AppData.currentContext)) {
				this.m_view = new IndiagramView(AppData.currentContext);
				this.m_view.setIndiagram(this);
			}
			return this.m_view;
		}
	}

	protected static Indiagram findParentResult = null;

	public static synchronized Category getParent(Indiagram _indiagram) {
		Category home = AppData.homeCategory;

		findParentResult = null;
		findParent(_indiagram, home);
		return (Category) findParentResult;
	}

	protected static boolean findParent(Indiagram _search, Indiagram _root) {
		if (_root.equals(_search)) {
			return true;
		}

		if (_root instanceof Category) {
			Category c = (Category) _root;
			for (int i = 0; i < c.indiagrams.size(); ++i) {
				if (findParent(_search, c.indiagrams.get(i))) {
					if (findParentResult == null) {
						findParentResult = _root;
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Retourne le chemin complet vers l'image
	 */
	public String getAbsoluteImagePath() {
		return PathData.IMAGE_DIRECTORY + this.imagePath;
	}

	/**
	 * Retourne l'image sous forme de Bitmap
	 */
	public Bitmap getImageAsBitmap() {
		String path = getAbsoluteImagePath();
		if (path != null) {
			Bitmap img = BitmapFactory.decodeFile(path);
			return img;
		}
		return null;
	}
}