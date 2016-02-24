	public void Scheduling(List<ScheduleRequest> srs, Map<String, Double> locationValueMap, HashMap<String, Integer> contentScoreMap) {
		// this predefine is kind of unneccessary, just sort locations by their ids, can just be ignored
		// this map is for storing all ScheduleRequests on their locations <LocationId, List<SR>>
		Map<Integer, ArrayList<ScheduleRequest>> locationContents = new TreeMap<Integer, ArrayList<ScheduleRequest>>(
				new Comparator<Integer>() {
					@Override
					public int compare(Integer i1, Integer i2) {
						return i2.compareTo(i1);
					}
				});

		// Insert all ScheduleRequests under their corresponding locations
		for (ScheduleRequest c : srs) {
			if (locationContents.containsKey(c.locationID)) {
				locationContents.get(c.locationID).add(c);
			} else {
				ArrayList<ScheduleRequest> cs = new ArrayList<ScheduleRequest>();
				cs.add(c);
				locationContents.put(c.Lid, cs);
			}
		}

		// Sort ScheduleRequests for every location by their start time
		for (ArrayList<ScheduleRequest> cs : locationContents.values()) {
			Collections.sort(cs, new Comparator<ScheduleRequest>() {
				@Override
				public int compare(ScheduleRequest c1, ScheduleRequest c2) {
					return ((Integer) c1.startTime).compareTo((Integer) c2.startTime);
				}
			});
			removeInvalid(cs, contentScoreMap);
		}
		IO.writeAds(locationContents.values());
		return;// locationContents;
	}

	public void removeInvalid(ArrayList<ScheduleRequest> contents, HashMap<String, Integer> contentScoreMap) {
		PriorityQueue<ScheduleRequest> queue = new PriorityQueue<ScheduleRequest>(
				new Comparator<ScheduleRequest>() {
					@Override
					public int compare(ScheduleRequest c1, ScheduleRequest c2) {
						return ((Integer) c1.endTime).compareTo((Integer) c2.endTime);
					}
				});

		ArrayList<ScheduleRequest> badContents = new ArrayList<ScheduleRequest>();
		int overlapCount = 0;

		// Traverse every ScheduleRequest sorted by their start time
		for (ScheduleRequest c1 : contents) {
			// Find all the ScheduleRequests which start time lay in time interval of
			// current ScheduleRequest
			// If start time of current ScheduleRequest is greater than current minimum
			// end time of previous ScheduleRequest,
			// poll front ScheduleRequest of queue till the end time of queue front is
			// larger than start time of current ScheduleRequest
			while (!queue.isEmpty() && c1.startTime >= queue.peek().endTime) {
				queue.poll();
				overlapCount--;
			}

			// Check if there is a duplicate in current ScheduleRequests
			// TODO: Keep the one with greater value, for now just skip detected
			// duplicate one
			ScheduleRequest badContent = isSameContent(queue, c1, contentScoreMap);
			if (badContent != null) {
				badContents.add(badContent);
			}
			// Put current ScheduleRequest into queue when there occurs a conflict and
			// current
			// ScheduleRequest has greater value or there is not a conflict at all.
			if (badContent == c1 || badContent == null) {
				queue.offer(c1);
			}
			// If there is no conflict, increment the overlap count
			if (badContent == null) {
				overlapCount++;
			}
			// If overlapped contents exceed 4, remove the one with minimum
			// value from both priorityqueue and original ScheduleRequest list
			if (overlapCount >= ) {
				ScheduleRequest contentWithMinValue = queue.peek();
				for (ScheduleRequest c : queue) {
					if (contentScoreMap.get(c.id) < contentScoreMap.get(contentWithMinValue.id)) {
						contentWithMinValue = c;
					}
				}
				queue.remove(contentWithMinValue);
				badContents.add(contentWithMinValue);
				overlapCount--;
			}
		}
		contents.removeAll(badContents);
	}

	public ScheduleRequest isSameContent(PriorityQueue<ScheduleRequest> q, ScheduleRequest content, HashMap<String, Integer> contentScoreMap) {
		for (ScheduleRequest c : q) {
			if (c.id == content.id) {
				return contentScoreMap.get(c.id) < contentScoreMap.get(content.id) ? c : content;
			}
		}
		return null;
	}