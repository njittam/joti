package com.umbrella.jotiwa.communication.interaction.area348;

import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.LinkBuilder;
import com.umbrella.jotiwa.communication.enumeration.area348.Area348_API;
import com.umbrella.jotiwa.communication.enumeration.area348.Keywords;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.InteractionManager;
import com.umbrella.jotiwa.communication.interaction.InteractionRequest;
import com.umbrella.jotiwa.communication.interaction.InteractionResult;
import com.umbrella.jotiwa.communication.interaction.InteractionResultState;
import com.umbrella.jotiwa.communication.interaction.OnRequestTaskCompleted;
import com.umbrella.jotiwa.data.objects.area348.sendables.HunterInfoSendable;
import com.umbrella.jotiwa.map.area348.handling.AsyncDataProcessingTask;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stesi on 22-9-2015.
 * Class for creating the request and queing it.
 */
public class DataUpdater extends InteractionManager implements OnRequestTaskCompleted {

    /**
     *
     */
    public DataUpdater() {
        super();
        setOnRequestTaskCompletedListener(this);
    }


    public Date lastHunterUpdate;

    /**
     * @param mapPart
     */
    public void update(MapPart mapPart) {
        update(mapPart, TeamPart.None);
    }

    /**
     * Updates a certain map part.
     * <p/>
     * TODO: Interaction is now instant, is this smart?
     * TODO: Implement hunter interaction.
     *
     * @param mapPart
     * @param teamPart
     */
    public void update(MapPart mapPart, TeamPart teamPart) {
        /**
         * Make sure the root of the LinkBuilder is set to the Area348 one.
         * */
        LinkBuilder.setRoot(Area348_API.root);
        switch (mapPart) {
            case All:
                this.update(MapPart.Vossen, teamPart);
                this.update(MapPart.Hunters, TeamPart.None);
                this.update(MapPart.ScoutingGroepen, TeamPart.None);
                this.update(MapPart.FotoOpdrachten, TeamPart.None);
                break;
            case Vossen:
                if (teamPart == TeamPart.All) {
                    String[] teamChars = new String[]{"a", "b", "c", "d", "e", "f", "x"};
                    for (int i = 0; i < teamChars.length; i++) {
                        super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), teamChars[i], Keywords.All}), null));
                    }
                    return;
                }
                super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), teamPart.getSubChar(), Keywords.All}), null));
                break;
            case Hunters:
                HunterInfoSendable hunterInfoSendable = HunterInfoSendable.get();
                /**
                 * Checks if the name is set of the user.
                 * If set send the name of the user with as excluder.
                 * If not, just send a normal request to get all hunters.
                 * */
                if (!hunterInfoSendable.gebruiker.matches(JotiApp.getNoUsername())) {
                    /**
                     * Checks if there was already a hunter update preformed.
                     * If so send the date with the request, so that we only get the NEW data.
                     * Else just queue a normal request with the name of the hunter as exclude.
                     * */
                    if (lastHunterUpdate != null) {
                        super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), Keywords.Special, hunterInfoSendable.gebruiker, lastHunterUpdate.toString()}), null));
                    } else {
                        super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), Keywords.Special, hunterInfoSendable.gebruiker}), null));
                    }
                } else {
                    super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), Keywords.All}), null));
                }
                lastHunterUpdate = new Date();
                break;
            case ScoutingGroepen:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), Keywords.All}), null));
                break;
            case FotoOpdrachten:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[]{mapPart.getValue(), Keywords.All}), null));
                break;
        }
    }

    /**
     * @param results
     */
    @Override
    public void onRequestTaskCompleted(ArrayList<InteractionResult> results) {
        ArrayList<InteractionResult> successful = new ArrayList<>();
        /**
         * Loop through each result.
         * */
        for (int i = 0; i < results.size(); i++) {
            /**
             * Check if the request was succesfull.
             * */
            if (results.get(i).getResultState() == InteractionResultState.INTERACTION_RESULT_STATE_SUCCESS) {
                successful.add(results.get(i));
            } else {
                /**
                 * Unsuccessful.
                 * TODO: Add UI notifier. The user should know there was a error.
                 * */
                System.out.print("Interaction was unsuccessful.");
            }
        }
        InteractionResult[] successfulArray = new InteractionResult[successful.size()];
        successful.toArray(successfulArray);
        new AsyncDataProcessingTask().execute(successfulArray);
    }
}
