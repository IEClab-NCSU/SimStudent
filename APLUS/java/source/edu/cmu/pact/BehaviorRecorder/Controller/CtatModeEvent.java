/*
 * Created on Jan 10, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.ctat.model.CtatModeModel;

/**
 * This is the class reponsible for communicating the actions 
 * between the controller and the views on the controller.
 * 
 * @author mpschnei
 *
 * Created on: Mar 31, 2006
 */
public class CtatModeEvent {

    public static class SetSimStudentActivationMenuEvent extends CtatModeEvent {
        public boolean newStatus;
        public SetSimStudentActivationMenuEvent(boolean newStatus) {
            this.newStatus = newStatus;
        }
    }


    public static class SetVisibleEvent extends CtatModeEvent {
        private final boolean visible;
        public SetVisibleEvent(boolean visible) {
            this.visible = visible;
        }
        /**
         * @return the {@link #visible}
         */
        public boolean isVisible() {
            return visible;
        }
    }

    public static final CtatModeEvent REPAINT = new RepaintEvent();
    public static final CtatModeEvent CLEAR_DRAWING_AREA = new ClearDrawingAreaEvent();


    public static class SetModeEvent extends CtatModeEvent {
        private String mode;
        private String previousMode;

        /** Now-current author mode. */
        private String authorMode;

        /** Pre-event author mode. */
        private String previousAuthorMode;

        /**
         * Construct the event.
         * @param mode
         * @param previousMode
         * @param authorMode
         * @param previousAuthorMode
         */
        public SetModeEvent(String mode, String previousMode,
                String authorMode, String previousAuthorMode) {
            setMode(mode);
            this.previousMode = previousMode;
            setAuthorMode(authorMode);
            this.previousAuthorMode = previousAuthorMode;
        }

        /**
         * Set {@link #mode}.
         * @param mode The mode to set.
         */
        private void setMode(String mode) {
            if (! CtatModeModel.getModeTypeList().contains(mode))
                throw new RuntimeException ("Unknown mode type: " + mode);
            this.mode = mode;
        }

        /**
         * Set {@link #authorMode}.
         * @param authorMode
         */
        private void setAuthorMode(String authorMode) {
            if (! CtatModeModel.getAuthorModeList().contains(authorMode))
                throw new RuntimeException ("Unknown author mode: " + authorMode);
            this.authorMode = authorMode;
        }

        /**
         * @return Returns the mode.
         */
        public String getMode() {
            return mode;
        }

        public String getPreviousMode() {
            return previousMode;
        }

        public String toString() {
            return ("Set mode from: "+previousMode+" to: "+mode+
                    "; authorMode from: "+previousAuthorMode+" to: "+authorMode);
        }
        public String getAuthorMode() {
            return authorMode;
        }
        public String getPreviousAuthorMode() {
            return previousAuthorMode;
        }

        /**
         * Convenience test to see if {@link #mode} changed.
         * @return true if {@link #mode} != {@link #previousMode}
         */
        public boolean modeChanged() {
            return !(getMode().equalsIgnoreCase(getPreviousMode()));
        }

        /**
         * Convenience test to see if {@link #authorMode} changed.
         * @return true if {@link #authorMode} != {@link #previousAuthorMode}
         */
        public boolean authorModeChanged() {
            return !(getAuthorMode().equalsIgnoreCase(getPreviousAuthorMode()));
        }
    }


    public static class ClearDrawingAreaEvent extends CtatModeEvent {}

    public static class RepaintEvent extends CtatModeEvent {}


    public static class SetCurrentNodeEvent extends CtatModeEvent {

        public ProblemNode previousCurrentNode;
        public ProblemNode newCurrentNode;

        public SetCurrentNodeEvent(ProblemNode currentNode, ProblemNode previousCurrentNode) {
            this.newCurrentNode = currentNode;
            this.previousCurrentNode = previousCurrentNode;
        }

    }

    public String toString() {
        return getClass().getName();
    }

}
