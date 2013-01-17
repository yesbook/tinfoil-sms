/** 
 * Copyright (C) 2011 Tinfoilhat
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tinfoil.sms.dataStructures;

import java.util.ArrayList;

public class ContactParent {

	//private boolean trusted;
	private String name;
	private ArrayList<ContactChild> numbers;
	//private boolean trusted;
	
	public ContactParent(String name, ArrayList<ContactChild> numbers)
	{
		this.setName(name);
		//this.setTrusted(trusted);
		this.numbers = numbers;
	}

	public boolean isTrusted() {
		for(int i = 0; i < numbers.size();i++)
		{
			if(numbers.get(i).isTrusted())
			{
				return true;
			}
		}
		return false;
	}

	/*public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ContactChild> getNumbers() {
		return numbers;
	}
	
	public ContactChild getNumber(int index)
	{
		return numbers.get(index);
	}

	public void setNumbers(ArrayList<ContactChild> numbers) {
		this.numbers = numbers;
	}
}