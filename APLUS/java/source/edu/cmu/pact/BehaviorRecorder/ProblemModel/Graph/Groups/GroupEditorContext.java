package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;

public class GroupEditorContext implements GroupChangeListener {
	Set<EditorContextListener> listeners;
	Map<LinkGroup, SingleGroupEditorContextInfo> groupInfoMap;
	Map<ExampleTracerLink, SingleLinkEditorContextInfo> linkInfoMap;
	
	Map<ProblemEdge, Set<Shape>> edgeToShapesMap;
	Map<Shape, LinkGroup> shapeToGroupMap;
	
	GroupModel groupModel;
	LinkGroup selectedGroup;
	
	public GroupEditorContext(GroupModel groupModel) {
		listeners = new HashSet<EditorContextListener>();		
		this.groupModel = groupModel;		
		groupModel.addGroupChangeListener(this);
		clear();		
	}
	
	public GroupModel getGroupModel() {
		return groupModel;
	}
	
	public void addEditorContextListener(EditorContextListener listener) {
		listeners.add(listener);}
	public void removeEditorContextListener(EditorContextListener listener) {
		listeners.remove(listener);}
	private void notifyListeners(EditContextEvent e) {
		for(EditorContextListener listener : listeners) {
			listener.editorContextChanged(e);
		}
	}
	
	public void addEdgeShapeGroupMap(ProblemEdge edge, Shape shapeToAdd, LinkGroup group) {
		Set<Shape> shapes = edgeToShapesMap.get(edge);
		if(shapes==null)
			shapes = new HashSet<Shape>();
		shapes.add(shapeToAdd);
		edgeToShapesMap.put(edge, shapes);
		shapeToGroupMap.put(shapeToAdd, group);
	}
	public void resetProblemEdgeMap(ProblemEdge target) {
		Set<Shape> shapes = edgeToShapesMap.get(target);
		if(shapes!=null) {
			for(Shape shape : shapes) {
				shapeToGroupMap.remove(shape);
			}
		}
		edgeToShapesMap.remove(target);
	} 
	public List<LinkGroup> getDisplayedGroupsByLink(ExampleTracerLink link) {
		ArrayList<LinkGroup> groupsForLink = new ArrayList<LinkGroup>();
		LinkGroup currentGroup = null;
		if(groupModel.isLinkInGroup(groupModel.getTopLevelGroup(), link))
			currentGroup = groupModel.getTopLevelGroup();
		for(int i = groupModel.getHeight()-1; i>=0; i--) {
			if(getGroupIsDisplayedOnGraph(currentGroup))
				groupsForLink.add(currentGroup);
			
			Set<LinkGroup> subgroups = groupModel.getGroupSubgroups(currentGroup);
			currentGroup=null;
			if (subgroups != null) {
				for(LinkGroup subgroup : subgroups) {				
					if(groupModel.isLinkInGroup(subgroup, link)) {
						currentGroup = subgroup;
						break;
					}
				}		
			}
			if(currentGroup==null)
				return groupsForLink;
		}
		return null;
	}
	public LinkGroup getGroupByPointOnGraph(Point p) {
		for(Set<Shape> shapes: edgeToShapesMap.values()) {
			for(Shape shape : shapes) {
				if(shape.contains(p)) {
					return shapeToGroupMap.get(shape);
				}						
			}			
		}
		return null;
	}
	
	public Set<ExampleTracerLink> getSelectedLinks() {
		Set<ExampleTracerLink> selectedLinks = new HashSet<ExampleTracerLink>();
		for(ExampleTracerLink link : groupModel.getGroupLinks(groupModel.getTopLevelGroup()))
			if(getLinkIsSelected(link))
				selectedLinks.add(link);
		return selectedLinks;
	}
	
	public LinkGroup getSelectedGroup() {
		return selectedGroup;}
	public void setSelectedGroup(LinkGroup group) {
		selectedGroup = group;
		notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
	}
	
	public Color getGroupColor(LinkGroup group) {
		return getGroupContextInfo(group).color;}
	public void setGroupColor(LinkGroup group, Color c) {
		getGroupContextInfo(group).color = c;
		notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
	}
	
	public boolean getGroupIsHovered(LinkGroup group) {
		return getGroupContextInfo(group).isHovered;}
	public void setGroupIsHovered(LinkGroup group, boolean isHovered) {
		getGroupContextInfo(group).isHovered = isHovered;
		notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
	}
	
	public boolean getGroupIsDisplayedOnGraph(LinkGroup group) {
		return getGroupContextInfo(group).isDisplayedOnGraph;}
	public void setGroupIsDisplayedOnGraph(LinkGroup group, boolean isDisplayedOnGraph) {
		getGroupContextInfo(group).isDisplayedOnGraph = isDisplayedOnGraph;
		notifyListeners(new EditContextEvent(EditContextEvent.SHOWNONGRAPH));
	}
	
	public boolean getGroupIsExpanded(LinkGroup group) {
		return getGroupContextInfo(group).isExpanded;}
	public void setGroupIsExpanded(LinkGroup group, boolean isExpanded) {
		getGroupContextInfo(group).isExpanded = isExpanded;
		notifyListeners(new EditContextEvent(EditContextEvent.EXPANSION));
	}
	
	private SingleGroupEditorContextInfo getGroupContextInfo(LinkGroup group) {
		if(groupInfoMap.get(group)==null)
			groupInfoMap.put(group, new SingleGroupEditorContextInfo());
		return groupInfoMap.get(group);
	}
	
	public boolean getLinkIsSelected(ExampleTracerLink link) {
		return getLinkContextInfo(link).isSelected;}
	public void setLinkIsSelected(ExampleTracerLink link, boolean isSelected) {
		getLinkContextInfo(link).isSelected = isSelected;
		notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
	}
	
	public boolean getLinkIsHovered(ExampleTracerLink link) {
		return getLinkContextInfo(link).isHovered;}
	public void setLinkIsHovered(ExampleTracerLink link, boolean isHovered) {
		getLinkContextInfo(link).isHovered = isHovered;
		notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
	}
	
	private SingleLinkEditorContextInfo getLinkContextInfo(ExampleTracerLink link) {
		if(linkInfoMap.get(link)==null)
			linkInfoMap.put(link, new SingleLinkEditorContextInfo());
		return linkInfoMap.get(link);
	}
	public void groupChanged(GroupChangeEvent e) {
		if(e.getEventType()==GroupChangeEvent.GROUPDELETED) {			
			groupInfoMap.remove(e.getGroupTargeted());
			if(selectedGroup==null)
				return;
			if(selectedGroup.equals(e.getGroupTargeted())) {					
				selectedGroup=null;
				notifyListeners(new EditContextEvent(EditContextEvent.OTHER));
			}
		}		
		if(e.getEventType() == GroupChangeEvent.GROUPSCLEARED)
			setGroupIsDisplayedOnGraph(groupModel.getTopLevelGroup(),false);
	}

	public ExampleTracerLink getLinkFromEdge(ProblemEdge edge) {
		for(ExampleTracerLink link : groupModel.getGroupLinks(groupModel.getTopLevelGroup())) {
			try {
				if(link.getEdge().getEdge().equals(edge))
					return link;
			}
			catch(Exception e){}
		}
		return null;
	}

	public void clear() {
		groupInfoMap = new HashMap<LinkGroup, SingleGroupEditorContextInfo>();
		linkInfoMap = new HashMap<ExampleTracerLink, SingleLinkEditorContextInfo>();
		
		edgeToShapesMap = new HashMap<ProblemEdge, Set<Shape>>();
		shapeToGroupMap = new HashMap<Shape, LinkGroup>();
		setGroupIsDisplayedOnGraph(groupModel.getTopLevelGroup(),false);
	}
	
	
}
