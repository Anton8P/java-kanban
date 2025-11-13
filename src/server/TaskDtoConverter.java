package server;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class TaskDtoConverter {

    public TaskDtoConverter() {
    }

    public static Task toEntity(TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }
        Task task;
        switch (taskDto.getType()) {
            case TASK:
                task = new Task(taskDto.getTitle(), taskDto.getDescription());
                break;
            case EPIC:
                task = new Epic(taskDto.getTitle(), taskDto.getDescription());
                break;
            case SUBTASK:
                if (taskDto.getEpicId() == null) {
                    throw new IllegalArgumentException("Subtask must have an epic id");
                }
                task = new Subtask(taskDto.getTitle(), taskDto.getDescription(), taskDto.getEpicId());
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + taskDto.getType());
        }

        if (taskDto.getId() != null && taskDto.getId() > 0) {
            if (task instanceof Subtask) {
                task = ((Subtask) task).withId(taskDto.getId());
            } else if (task instanceof Epic) {
                task = ((Epic) task).withId(taskDto.getId());
            } else {
                task = task.withId(taskDto.getId());
            }
        }

        if (taskDto.getStatus() != null) {
            if (task instanceof Subtask) {
                task = ((Subtask) task).withStatus(taskDto.getStatus());
            } else if (task instanceof Epic) {
                task = ((Epic) task).withStatus(taskDto.getStatus());
            } else {
                task = task.withStatus(taskDto.getStatus());
            }
        }

        if (!(task instanceof Epic)) {
            if (taskDto.getStartTime() != null) {
                if (task instanceof Subtask) {
                    task = ((Subtask) task).withStartTime(taskDto.getStartTime());
                } else {
                    task = task.withStartTime(taskDto.getStartTime());
                }
            }

            if (taskDto.getDuration() != null && taskDto.getDuration() > 0) {
                if (task instanceof Subtask) {
                    task = ((Subtask) task).withDuration(taskDto.getDuration());
                } else {
                    task = task.withDuration(taskDto.getDuration());
                }
            }
        }

        return task;
    }

    public static TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setType(task.getType());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setStartTime(task.getStartTime());


        if (task.getDuration() != null) {
            taskDto.setDuration(task.getDuration().toMinutes());
        } else {
            taskDto.setDuration(0L);
        }

        if (task instanceof Subtask) {
            taskDto.setEpicId(((Subtask) task).getEpicId());
        }
        return taskDto;
    }
}

