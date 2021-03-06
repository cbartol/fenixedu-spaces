/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Spaces.
 *
 * FenixEdu Spaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Spaces is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.spaces.ui;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@BennuSpringController(SpacesController.class)
@RequestMapping("/spaces/occupations")
public class OccupationController {

    private final JsonParser jsonParser = new JsonParser();

    @Autowired
    OccupationService occupationService;

    private List<Interval> parseIntervals(String events) {
        final JsonArray jsonEvents = jsonParser.parse(events).getAsJsonArray();
        List<Interval> intervals = new ArrayList<>();
        for (JsonElement jsonEvent : jsonEvents) {
            final JsonObject event = jsonEvent.getAsJsonObject();
            final Long start = Long.parseLong(event.get("start").getAsString()) * 1000L;
            final Long end = Long.parseLong(event.get("end").getAsString()) * 1000L;
            intervals.add(new Interval(new DateTime(start), new DateTime(end)));
        }
        return intervals;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String getCreate(Model model, @RequestParam(required = false) OccupationRequest request) {
        model.addAttribute("request", request);
        return "occupations/create";
    }

    @RequestMapping(value = "search-create", method = RequestMethod.POST)
    public String searchSpaces(Model model, @RequestParam String events, @RequestParam String config, @RequestParam(
            required = false) OccupationRequest request) {
        final List<Interval> intervals = parseIntervals(events);
        model.addAttribute("events", events);
        model.addAttribute("config", config);
        model.addAttribute("request", request);
        model.addAttribute("freeSpaces", occupationService.searchFreeSpaces(intervals, Authenticate.getUser()));
        return "occupations/searchcreate";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @RequestParam String emails, @RequestParam String subject,
            @RequestParam String description, @RequestParam String selectedSpaces, @RequestParam String config,
            @RequestParam String events, @RequestParam(required = false) OccupationRequest request) {
        try {
            occupationService.createOccupation(emails, subject, description, selectedSpaces, config, events, request);
            if (request != null) {
                return "redirect:/spaces/occupations/requests/" + request.getExternalId();
            }
            return "redirect:/spaces/occupations/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return searchSpaces(model, events, config, request);
        }
    }

    @RequestMapping("view/{occupation}")
    public String view(Model model, @PathVariable Occupation occupation) {
        model.addAttribute("occupation", occupation);
        model.addAttribute("events", occupationService.exportEvents(occupation));
        model.addAttribute("config", occupationService.exportConfig(occupation));
        model.addAttribute("freeSpaces", occupationService.getFreeAndSelectedSpaces(occupation, Authenticate.getUser()));
        return "occupations/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String edit(Model model, @RequestParam Occupation occupation, @RequestParam String emails,
            @RequestParam String subject, @RequestParam String description, @RequestParam String selectedSpaces) {
        try {
            occupationService.editOccupation(occupation, emails, subject, description, selectedSpaces);
            return "redirect:/spaces/occupations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return view(model, occupation);
        }
    }

    @RequestMapping(value = "{occupation}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Occupation occupation) {
        occupationService.delete(occupation);
        return "redirect:/spaces/occupations/list";
    }

}
