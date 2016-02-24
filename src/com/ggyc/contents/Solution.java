package com.ggyc.contents;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import org.omg.CORBA.PUBLIC_MEMBER;

class TimeNode {
	int time;
	int isStart;
	int range;
	Content request;
	TimeNode endTimePair;

	TimeNode(int t, int isStart, int r, Content req) {
		time = t;
		this.isStart = isStart;
		range = r;
		request = req;
	}

	public static Comparator<TimeNode> scheduleComparator = new Comparator<TimeNode>() {
		@Override
		public int compare(TimeNode s1, TimeNode s2) {
			if (s1.time == s2.time) {
				return s1.isStart - s2.isStart;
			} else {
				return s1.time - s2.time;
			}
		}
	};

}

public class Solution {
	// Result of contents selection
	public ArrayList<Content> selectedContents = new ArrayList<Content>();

	// Initial weight value
	public int maxWeight = 0;

	/**
	 * @param contents
	 *            all contents in the schedule file
	 * @param locations
	 *            location collection mapping every location(id) to a multiplier
	 * @return An optimal schedule for all locations
	 */

	public HashMap<Integer, Content> Selection(ArrayList<Content> contents,
			Map<Integer, Integer> locations, int time) {
		HashMap<String, ArrayList<Content>> locationContents = new HashMap<Integer, ArrayList<Content>>();
		// Collections.sort(contents, new Comparator<Content>() {
		// @Override
		// public int compare(Content c1, Content c2) {
		// return c1.Lid < c2.Lid ? -1 : c1.Lid == c2.Lid ? 0 : 1;
		// }
		// });
		//a hashmap (time, (location, ScheduleRequest))
		HashMap<Integer, HashMap<String, ArrayList<ScheduleRequest>>> map = new HashMap<Integer, HashMap<String, ArrayList<ScheduleRequest>>>();

		Iterator<ScheduleRequest> iter = ReadIn();   //put all input into a HashMap
		while (iter.hasNext()) {   // traverse the SR in order 
			ScheduleRequest sr = iter.next();
        	for (int i = sr.start; i <= sr.end; i++) {
        		if (!map.containsKey(i)) {
        			map.put(i, new HashMap<String, ArrayList<ScheduleRequest>>());
        		}
        		if (!map.get(i).containsKey(sr.LocationId)) {  
        			map.get(i).put(sr.LocationId, new ArrayList<ScheduleRequest>());
        		}
				map.get(i).get(sr.LocationId).add(sr); 
        	}
        }
		locationContents = map.get(time);
//		for (Content c : contents) {
//			// If location is already hashed in
//			if (locationContents.containsKey(c.Lid)) {
//				int locationId = c.Lid;
//				locationContents.get(locationId).add(c);
//				// A new location coming up
//			} else {
//				ArrayList<Content> contentsOfLocation = new ArrayList<Content>();
//				contentsOfLocation.add(c);
//				locationContents.put(c.Lid, contentsOfLocation);
//			}
//		}

		for (ArrayList<Content> contentsOfLocation : locationContents.values()) {
			// Sort contents on an ad location by their value
			Collections.sort(contentsOfLocation, new Comparator<Content>() {
				@Override
				public int compare(Content c1, Content c2) {
					return c1.value < c2.value ? 1 : c1.value == c2.value ? 0
							: -1;
				}
			});
		}

		// Customized comparator if necessary which means locations id is not
		// related to multiplier
		// MyComparator comparator = new MyComparator(locations);
		//
		// // Treemap for sorted locations by descending multipliers
		// Map<Integer, Integer> lMap = new TreeMap<Integer,
		// Integer(comparator);
		// lMap.putAll(locations);

		TreeNode root = new TreeNode(new Content(-1));
		Queue<TreeNode> contentsQueue = new LinkedList<TreeNode>();
		contentsQueue.offer(root);
		// BFS to construct tree
		for (ArrayList<Content> cs : locationContents.values()) {
			int size = contentsQueue.size();
			while (size-- != 0) {
				TreeNode node = contentsQueue.poll();
				for (Content c : cs) {
					TreeNode newNode = new TreeNode(c);
					node.contents.add(newNode);
					contentsQueue.add(newNode);
				}
				Content content = new Content(0);
				content.setValue(0);
				TreeNode newNode = new TreeNode(content);
				node.contents.add(newNode); // Add a 0-value content branch
											// which represents some conflict
											// situation
				contentsQueue.add(newNode);
			}
		}

		helper(root, new ArrayList<Content>(), locations, 0);

		HashMap<Integer, Content> ret = new HashMap<Integer, Content>();
		// Insert location ID and content endTimePair from selected contents(by
		// location descending order) into result map
		int i = 0;
		for (Integer key : locations.keySet())
			ret.put(key, selectedContents.get(i++));

		return ret;
	}

	// TODO: Remove unnecessary search
	public void helper(TreeNode root, ArrayList<Content> list,
			Map<Integer, Integer> locations, int tmpValue) {
		if (root == null || list.contains(root.content))
			return;
		if (root.content.id != -1 && root.content.value >= 0) {
			tmpValue += root.content.value == 0 ? 0 : locations
					.get(root.content.Lid) * root.content.value;
		}
		if (root.contents.size() == 0 && tmpValue > maxWeight) {
			list.add(root.content);
			maxWeight = tmpValue;
			selectedContents = new ArrayList<Content>(list);
			list.remove(list.size() - 1);
			return;
		}
		for (TreeNode c : root.contents) {
			// Exclude the most top node
			if (root.content.id != -1 && root.content.value >= 0)
				list.add(root.content);
			helper(c, list, locations, tmpValue);
			// If not the first-level recursion(most top node)
			if (list.size() > 0)
				list.remove(list.size() - 1);
		}
	}

	/**
	 * Convert current contents schedule into a valid schedule
	 * 
	 * @param contents
	 * @param locations
	 * @return a map with key as locationID and value as contents belonging to
	 *         that location
	 */
	public Map<Integer, ArrayList<Content>> Scheduling(
			ArrayList<Content> scheduleRequest, Map<Integer, Integer> locations) {
		Map<Integer, ArrayList<Content>> locationContents = new HashMap<Integer, ArrayList<Content>>(); // new
																									// TreeMap<Integer,
																									// ArrayList<Content>>(
		// new Comparator<Integer>() {
		// @Override
		// public int compare(Integer i1, Integer i2) {
		// return i2.compareTo(i1);
		// }
		// });

		// Insert all contents under their corresponding location
		for (Content req : scheduleRequest) {
			if (locationContents.containsKey(req.getLocationId())) {
				locationContents.get(req.getLocationId()).add(req);
			} else {
				ArrayList<Content> requests = new ArrayList<Content>();
				requests.add(req);
				locationContents.put(req.getLocationId(), requests);
			}
		}

		// Sort contents for every location by their start time
		for (ArrayList<Content> requests : locationContents.values()) {
			Collections.sort(requests, new Comparator<Content>() {
				@Override
				public int compare(Content r1, Content r2) {
					return r1.getStartTime() != r2.getStartTime() ? r1.getStartTime() - r2.getStartTime() : r1.getEndTime()
							- r2.getEndTime();
				}
			});
			removeInvalidSchedules(requests);
		}
		return locationContents;
	}

	// Method 2;
	// Put starts and ends of all the intervals together, and then sort them with marking the attribute(start or end). Then we can convert this problem as a problem of nested parenthesis
	// matching. we regard start time as left parenthesis and end time as right parenthesis. We loop through the sorted time with a counter, when it's start we plus 1, subtract 1 when it's end.
	// In the process of recording the count, when this count exceed 3 or when the duplicate happens within a valid range, we just ignore it. For duplicate detect, I plan to use hashset.
	public void removeInvalidSchedules(ArrayList<Content> requests) {
		List<TimeNode> timeList = new ArrayList<TimeNode>();
		List<Content> badRequests = new ArrayList<Content>();
		for (Content req : requests) {
			TimeNode start = new TimeNode(req.getStartTime(), 1, req.getEndTime() - req.getStartTime(), req);
			TimeNode end = new TimeNode(req.getEndTime(), 0, req.getEndTime() - req.getEndTime(), req);
			start.endTimePair = end;
			timeList.add(start);
			timeList.add(end);
		}
		HashSet<String> set = new HashSet<String>();
		Collections.sort(timeList, TimeNode.scheduleComparator);
		// int count = 0;
		for (TimeNode node : timeList) {
			if (node.isStart == 1) {
				// Not include same content, content.content means the "ID" of
				// the content
				if (set.size() < 3 && !set.contains(node.request.getContentId())) {
					// count++;
					// take a pick
					set.add(node.request.getContentId());
				}
				// If exceed 3 at the same time or there are same contents overlapped
				else {
					// Avoid subtract one more time if schedule duplicate by marking
					// the isStart in ')' schedule with -1
					node.endTimePair.isStart = -1;
					//rejectFile.writetofile(request)
					badRequests.add(node.request);
				}
				// Add to check duplicate
			}
			// means the corresponding schedule has been added and
			// there is no duplicate within current VALID range(the range
			// containing less than 4 ads).
			else if (node.isStart == 0) {
				// count--;
				// remove the content when get a ')'
				set.remove(node.request.getContentId());
			}
		}
		requests.removeAll(badRequests);
	}

	public void removeInvalid(ArrayList<Content> requests) {
		PriorityQueue<Content> queue = new PriorityQueue<Content>(
				new Comparator<Content>() {
					@Override
					public int compare(Content c1, Content c2) {
						return ((Integer) c1.end).compareTo((Integer) c2.end);
					}
				});
		ArrayList<Content> badRequests = new ArrayList<Content>();
		// int overlapCount = 0;
		Iterator<Content> it = requests.iterator();
		// Traverse every content sorted by their start time
		while (it.hasNext()) {
			Content req = it.next();
			// Find all the contents which start time lay in time interval of
			// current content
			// If start time of current content is greater than and equal to
			// current minimum
			// end time of previous Contents,
			// poll front Content of queue till the end time of queue front is
			// larger than start time of current Content
			while (!queue.isEmpty() && req.getStartTime() >= queue.peek().getEndTime()) {
				queue.poll();
				// overlapCount--;
			}

			// Check if there is a duplicate in current contents
			// TODO: Keep the one with greater value, for now just skip detected
			// duplicate one
			// Content badContent = isSameContent(queue, c1);
			if (!hasSameContent(queue, req) && queue.size() < 3) {// (badContent
																	// != null)
																	// {
				queue.offer(req);
			} else {
				it.remove();
				badRequests.add(req);
			}
			// Put current content into queue when there occurs a conflict and
			// current
			// content has greater value or there is not a conflict at all.
			// if (badContent == null || badContent == c1) {
			// queue.offer(c1);
			// }
			// If there is no conflict, increment the overlap count
			// if (badContent == null) {
			// overlapCount++;
			// }
			// If overlapped contents exceed 4, remove the one with minimum
			// value from both priorityqueue and original content list
			// if (queue.size() >= 4) {
			// Content contentWithMinValue = queue.peek();
			// for (Content c : queue) {
			// if (c.value < contentWithMinValue.value) {
			// contentWithMinValue = c;
			// }
			// }
			// queue.remove(contentWithMinValue);
			// badContents.add(contentWithMinValue);
			// //overlapCount--;
			// }
		}
		// contents.removeAll(badContents);
	}

	public boolean hasSameContent(PriorityQueue<Content> q, Content content) {
		for (Content c : q) {
			if (c.getContentId() == content.getContentId()) {
				return true;
			}
		}
		return false;
	}

	public Content isSameContent(PriorityQueue<Content> q, Content content) {
		for (Content c : q) {
			if (c.content == content.content) {
				return c.value < content.value ? c : content;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// Create three locations for content display
		Location l1 = new Location(1);
		Location l2 = new Location(2);
		Location l3 = new Location(3);
		l1.setWeight(5);
		l2.setWeight(3);
		l3.setWeight(1);

		// Create content schedule
		Content c1 = new Content(1);
		Content c2 = new Content(2);
		Content c3 = new Content(3);
		Content c4 = new Content(4);
		Content c5 = new Content(5);
		Content c6 = new Content(6);
		Content c7 = new Content(7);
		Content c8 = new Content(8);
		Content c9 = new Content(9);
		c1.setLocationID(l1.id);
		c2.setLocationID(l1.id);
		c3.setLocationID(l1.id);
		c4.setLocationID(l2.id);
		c5.setLocationID(l2.id);
		c6.setLocationID(l2.id);
		c7.setLocationID(l2.id);
		c8.setLocationID(l2.id);
		c9.setLocationID(l3.id);

		c1.setValue(7);
		c2.setValue(4);
		c3.setValue(8);
		c4.setValue(3);
		c5.setValue(6);
		c6.setValue(5);
		c7.setValue(8);
		c8.setValue(1);
		c9.setValue(2);

		c1.setContent("A");
		c2.setContent("B");
		c3.setContent("G");
		c4.setContent("A");
		c5.setContent("F");
		c6.setContent("A");
		c7.setContent("E");
		c8.setContent("D");
		c9.setContent("B");

		c1.setStartTime(0);
		c2.setStartTime(1);
		c3.setStartTime(2);
		c4.setStartTime(2);
		c5.setStartTime(3);
		c6.setStartTime(5);
		c7.setStartTime(0);
		c8.setStartTime(0);
		c9.setStartTime(3);

		c1.setEndTime(3);
		c2.setEndTime(6);
		c3.setEndTime(10);
		c4.setEndTime(12);
		c5.setEndTime(8);
		c6.setEndTime(7);
		c7.setEndTime(6);
		c8.setEndTime(6);
		c9.setEndTime(6);

		ArrayList<Content> contents = new ArrayList<Content>();
		contents.add(c1);
		contents.add(c2);
		contents.add(c3);
		contents.add(c4);
		contents.add(c5);
		contents.add(c6);
		contents.add(c7);
		contents.add(c8);
		contents.add(c9);
		Map<Integer, Integer> locations = new HashMap<Integer, Integer>();
		locations.put(l1.id, l1.weight);
		locations.put(l2.id, l2.weight);
		locations.put(l3.id, l3.weight);
		Solution sl = new Solution();
		sl.print(sl.Scheduling(contents, locations));
		sl.printSelection(sl.Selection(contents, locations));

	}

	public void print(Map<Integer, ArrayList<Content>> m) {
		for (Map.Entry endTimePair : m.entrySet()) {
			System.out.print("Location ID: " + endTimePair.getKey() + " ");
			System.out.print("Contents: ");
			for (Content c : ((ArrayList<Content>) endTimePair.getValue()))
				System.out.print(c.id);
			System.out.println();
		}
	}

	public void printSelection(Map<Integer, Content> m) {
		for (Map.Entry endTimePair : m.entrySet()) {
			System.out.print("Location ID: " + endTimePair.getKey() + " ");
			System.out.println("Content: " + ((Content) endTimePair.getValue()).id);
		}
	}
}
