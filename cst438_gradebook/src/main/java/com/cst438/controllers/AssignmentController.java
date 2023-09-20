package com.cst438.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	AssignmentGradeRepository assignmentGradeRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignmentById(@PathVariable("id") int id) {
		Assignment as = assignmentRepository.findById(id).orElse(null);
		if(as == null) {
			return null;
		}
		return new AssignmentDTO(as.getId(), as.getName(), as.getDueDate().toString(), as.getCourse().getTitle(), as.getCourse().getCourse_id());
	}
	
	@SuppressWarnings("deprecation")
	@PostMapping("/assignment")
	public int newAssignment(@RequestBody AssignmentDTO as) {
		Assignment realAs = new Assignment();
		realAs.setName(as.assignmentName());
		realAs.setId(as.id());

		int year = Integer.parseInt(as.dueDate().substring(0,4));
		int month = Integer.parseInt(as.dueDate().substring(5,7));
		int day = Integer.parseInt(as.dueDate().substring(8,10));
		realAs.setDueDate(new Date(Date.UTC(year - 1900, month-1, day+1, 0, 0, 0)));
		
		Course c = courseRepository.findById(as.courseId()).orElse(null);
		realAs.setCourse(c);
		
		assignmentRepository.save(realAs);
		System.out.println(realAs);
		return realAs.getId();
	}
	
	@PutMapping("/assignment/{id}")
	public AssignmentDTO updateAssignment(@PathVariable("id") Integer id, @RequestBody AssignmentDTO as) {
		Assignment realAs = new Assignment();
		realAs.setName(as.assignmentName());
		realAs.setId(as.id());

		int year = Integer.parseInt(as.dueDate().substring(0,4));
		int month = Integer.parseInt(as.dueDate().substring(5,7));
		int day = Integer.parseInt(as.dueDate().substring(8,10));
		realAs.setDueDate(new Date(Date.UTC(year - 1900, month-1, day+1, 0, 0, 0)));
		
		Course c = courseRepository.findById(as.courseId()).orElse(null);
		realAs.setCourse(c);
		
		Assignment foundAssignment = assignmentRepository.findById(id).orElse(null);
		if(foundAssignment == null) {
			return null;
		}
		foundAssignment.setCourse(realAs.getCourse());
		foundAssignment.setDueDate(realAs.getDueDate());
		foundAssignment.setName(realAs.getName());
		assignmentRepository.save(foundAssignment);
		return new AssignmentDTO(foundAssignment.getId(), foundAssignment.getName(), foundAssignment.getDueDate().toString(), foundAssignment.getCourse().getTitle(), foundAssignment.getCourse().getCourse_id());
	}
	
	@DeleteMapping("/assignment/{id}")
	public boolean deleteAssignment(@PathVariable("id") Integer id, @RequestParam("force") Optional<String> force) {
		Assignment as = assignmentRepository.findById(id).orElse(null);
		if(as == null) 
			return false;
		
		AssignmentGrade[] grades = assignmentGradeRepository.findByAssignmentId(as.getId());
		if(grades.length > 0 && !force.isPresent()) {
			return false;
		} else if(grades.length > 0 && force.isPresent() && !force.get().equals("true")) {
			System.out.println(force.get());
			return false;
		}
		assignmentRepository.delete(as);
		return true;
	}
}
